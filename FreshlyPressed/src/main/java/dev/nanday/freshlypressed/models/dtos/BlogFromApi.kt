package dev.nanday.freshlypressed.models.dtos

import com.google.gson.annotations.SerializedName

data class BlogFromApi(

    val URL: String,

    @SerializedName(value = "subscribers_count")
    val subscribersCount : Int
)