package dev.nanday.freshlypressed.services

import android.util.Log
import dev.nanday.freshlypressed.FreshlyPressedApplication.Companion.API_ENDPOINT
import dev.nanday.freshlypressed.models.Post
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface BlogRepository {
    /**
     * Loads the subscribers count for given blog, if API or cache has the data
     */
    suspend fun loadSubscribersCount(post: Post): Int?
}

class BlogRepositoryImpl(
    private val dao: FreshlyPressedDao,
    private val dtoMapper: DtoMapper
) : BlogRepository {

    companion object {
        private const val TAG = "BlogRepositoryImpl"
    }

    private val apiService = Retrofit.Builder()
        .baseUrl(API_ENDPOINT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BlogApiService::class.java)

    override suspend fun loadSubscribersCount(post: Post): Int? {
        val blogUrl = post.authorUrl.host!!
        try {
            val apiBlog = apiService.getBlogInfo(blogUrl)
            // Updates local cache //
            val dbBlog = dtoMapper.convertApiBlogToDbBlog(apiBlog)
            dao.insertOrUpdateBlog(dbBlog)
            val blogModel = dtoMapper.convertApiBlogToBlogModel(apiBlog)
            return blogModel.subscribersCount
        } catch (e: HttpException) {
            Log.w(TAG, "loadSubscribersCount: HttpException occurred (may be missing connectivity): $e")
        } catch (e: Exception) {
            Log.e(TAG, "loadSubscribersCount: Exception occurred: $e")
        }
        // An error occurred //
        // Returns subscribers count from local cache, if available //
        val blogFromDb = dao.getBlog(blogUrl)
        if (blogFromDb != null) {
            return blogFromDb.subscribersCount
        }
        return null
    }
}