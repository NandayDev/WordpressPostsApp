package dev.nanday.freshlypressed.services

import android.net.Uri
import androidx.annotation.VisibleForTesting
import dev.nanday.freshlypressed.models.Blog
import dev.nanday.freshlypressed.models.Post
import dev.nanday.freshlypressed.models.dtos.BlogFromApi
import dev.nanday.freshlypressed.models.dtos.BlogFromDb
import dev.nanday.freshlypressed.models.dtos.PostFromApi
import dev.nanday.freshlypressed.models.dtos.PostFromDb
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility service to map between DTOs and application models
 */
open class DtoMapper {

    private val dateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
    }

    fun convertApiPostsToDbPosts(apiPosts: List<PostFromApi>): List<PostFromDb> {
        return apiPosts.map { postFromApi ->
            convertApiPostToDbPost(postFromApi)
         }
    }

    fun convertApiPostToDbPost(postFromApi: PostFromApi) : PostFromDb {
        return PostFromDb(
            title = postFromApi.title,
            excerpt = postFromApi.excerpt,
            authorName = postFromApi.author.name,
            date = postFromApi.date,
            authorUrl = postFromApi.author.URL,
            uri = postFromApi.URL,
            subscribersCount = null,
            imageUrl = postFromApi.imageUrl,
            imagePath = null
        )
    }

    fun convertDbPostsToModels(dbPosts: List<PostFromDb>): List<Post> {
        return dbPosts.map { dbPost ->
            Post(
                id = dbPost.id!!,
                title = dbPost.title,
                excerpt = dbPost.excerpt,
                authorName = dbPost.authorName,
                date = convertDateFromApiToLocalDate(dbPost.date),
                authorUrl = Uri.parse(dbPost.authorUrl),
                imageUrl = dbPost.imageUrl,
                subscribersCount = dbPost.subscribersCount
            )
        }
    }

    fun convertApiBlogToDbBlog(blogFromApi: BlogFromApi) : BlogFromDb {
        return BlogFromDb(
            url = blogFromApi.URL,
            subscribersCount = blogFromApi.subscribersCount
        )
    }

    fun convertApiBlogToBlogModel(blogFromApi: BlogFromApi): Blog {
        return Blog(
            url = blogFromApi.URL,
            subscribersCount = blogFromApi.subscribersCount
        )
    }

    fun convertDbBlogToBlogModel(blogFromDb: BlogFromDb): Blog {
        return Blog(
            url = blogFromDb.url,
            subscribersCount = blogFromDb.subscribersCount
        )
    }

    @VisibleForTesting
    private fun convertDateFromApiToLocalDate(dateFromApi: String): Date? {
        return dateFormat.parse(dateFromApi)
    }


}