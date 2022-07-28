package dev.nanday.freshlypressed.services

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import dev.nanday.freshlypressed.models.Post
import dev.nanday.freshlypressed.models.dtos.PostFromDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import ru.gildor.coroutines.okhttp.await
import java.io.File
import java.io.FileOutputStream
import java.util.*

interface PostRepository {

    /**
     * Loads the posts from the remote API or local cache
     * Also caches the posts for offline usage
     */
    suspend fun loadPosts(): List<Post>

    /**
     * Loads image for given post and caches it for offline usage
     */
    suspend fun loadImage(post: Post): ByteArray?
}

class PostRepositoryImpl(
    private val context: Context,
    private val dao: FreshlyPressedDao,
    private val apiService: PostApiService,
    private val dtoMapper: DtoMapper
) : PostRepository {

    companion object {
        const val TAG = "PostRepositoryImpl"
    }

    override suspend fun loadPosts(): List<Post> {
        var dbPosts: List<PostFromDb>? = null
        try {
            // Fetches the posts from the API //
            val apiPosts = apiService.getPosts()
            // Deletes all posts in the local cache and replaces them //
            dbPosts = dtoMapper.convertApiPostsToDbPosts(apiPosts.posts)
            dao.deleteAllPosts()
            val ids = dao.insertPosts(dbPosts)
            for (i in ids.indices) {
                dbPosts[i].id = ids[i]
            }
        } catch (e: HttpException) {
            Log.w(TAG, "loadPosts - HttpException occurred (may be missing connectivity): $e")
        } catch (e: Exception) {
            Log.e(TAG, "loadPosts - Exception occurred: $e")
        }

        if (dbPosts == null) {
            // An error occurred //
            // Returns posts from local cache, if available //
            dbPosts = dao.getAllPosts()
        }

        val posts = dtoMapper.convertDbPostsToModels(dbPosts)
        loadPostImagesIfAny(dbPosts, posts)
        return posts
    }

    private fun loadPostImagesIfAny(dbPosts: List<PostFromDb>, posts: List<Post>) {
        for (i in posts.indices) {
            val dbPost = dbPosts[i]
            if (dbPost.imagePath != null) {
                posts[i].image = BitmapFactory.decodeFile(dbPost.imagePath!!)
            }
        }
    }

    private val okHttp by lazy {
        OkHttpClient.Builder().build()
    }

    override suspend fun loadImage(post: Post): ByteArray? {
        return try {
            val request = Request.Builder()
                .url(post.imageUrl)
                .build()
            val response = okHttp.newCall(request).await()
            // TODO Replace inappropriate blocking methods? if possible //
            val imageBytes = response.body?.bytes()
            // Saves the image in cache //
            withContext(Dispatchers.IO) {
                val file = File.createTempFile(post.id.toString() + Date().time, "png", context.cacheDir)
                val fileStream = FileOutputStream(file)
                fileStream.write(imageBytes)
                fileStream.close()
                val dbPost = dao.getPostById(post.id)
                dbPost.imagePath = file.absolutePath
                dao.insertOrUpdatePost(dbPost)
            }
            imageBytes
        } catch(e: Exception) {
            Log.e(TAG, "loadImage - $e")
            null
        }
    }
}