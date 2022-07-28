package dev.nanday.freshlypressed.models.dtos

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val posts: List<PostFromApi>
)

data class PostFromApi(
    val title: String,
    val excerpt: String,
    val author: Author,
    val date: String,
    @SerializedName(value = "featured_image")
    val imageUrl: String,
    val URL: String
) {
    data class Author(
        val name: String,
        val URL: String
    )
}

