package dev.nanday.freshlypressed.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.format.DateFormat
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.nanday.freshlypressed.models.Post
import dev.nanday.freshlypressed.services.BlogRepository
import dev.nanday.freshlypressed.services.PostRepository
import dev.nanday.freshlypressed.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostListActivityViewModel(
    context: Context,
    private val postRepository: PostRepository,
    private val blogRepository: BlogRepository
) : ViewModel() {

    private val dateFormat = DateFormat.getDateFormat(context)

    private val _postsData = MutableLiveData<List<PostData>?>(null)
    val postsData: LiveData<List<PostData>?> = _postsData

    fun loadPosts() {
        viewModelScope.launch (Dispatchers.IO) {
            val posts = postRepository.loadPosts()
            val elements = convertPostsToAdapterElements(posts)
            _postsData.postValue(elements.toList())

            // Secondly, loads images and subscribers count //
            for (post in posts) {
                if (post.image == null && post.subscribersCount == null) {
                    val imageBitmap = loadImage(post)
                    val subscribers = loadSubscribersCounts(post)
                    val element = elements.first { it.id == post.id }
                    val index = elements.indexOf(element)
                    elements[index] = PostData(
                        id = element.id,
                        headerText = element.headerText,
                        title = element.title,
                        excerpt = element.excerpt,
                        authorName = element.authorName,
                        subscribersCount = subscribers?.toString() ?: "--",
                        image = imageBitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                    )
                    _postsData.postValue(elements.toList())
                }
            }
        }
    }

    private suspend fun loadImage(post: Post): Bitmap? {
        val imageByteArray = postRepository.loadImage(post)
        return if (imageByteArray != null) {
            BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        } else {
            null
        }
    }

    private suspend fun loadSubscribersCounts(post: Post): Int? {
        return blogRepository.loadSubscribersCount(post)
    }

    @VisibleForTesting
    private fun convertPostsToAdapterElements(posts: List<Post>): MutableList<PostData> {
        val elementsList = mutableListOf<PostData>()
        for (i in posts.indices) {
            val post = posts[i]
            val headerText = if (post.date != null) {
                if (i == 0 || !DateUtils.isSameDay(posts[i - 1].date, post.date)) {
                    dateFormat.format(post.date)
                } else {
                    null
                }
            } else {
                null
            }
            val element = PostData(
                post.id,
                headerText,
                post.title,
                post.excerpt,
                post.authorName,
                if (post.subscribersCount != null) post.subscribersCount.toString() else null,
                post.image
            )
            elementsList.add(element)
        }
        return elementsList
    }
}

class PostData(
    val id: Long,
    val headerText: String?,
    val title: String,
    val excerpt: String,
    val authorName: String,
    val subscribersCount: String?,
    val image: Bitmap?
)