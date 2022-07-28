package dev.nanday.freshlypressed.services

import dev.nanday.freshlypressed.models.dtos.PostResponse
import retrofit2.http.GET

interface PostApiService {

    @GET("discover.wordpress.com/posts?number=10")
    suspend fun getPosts(): PostResponse
}