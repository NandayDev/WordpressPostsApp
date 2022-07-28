package dev.nanday.freshlypressed.services

import dev.nanday.freshlypressed.models.dtos.BlogFromApi
import retrofit2.http.GET
import retrofit2.http.Path

interface BlogApiService {

    @GET("{blogUrl}")
    suspend fun getBlogInfo(@Path(value = "blogUrl") blogUrl: String) : BlogFromApi

}