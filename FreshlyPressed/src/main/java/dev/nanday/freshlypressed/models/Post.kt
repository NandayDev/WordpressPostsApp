package dev.nanday.freshlypressed.models

import android.graphics.Bitmap
import android.net.Uri
import java.util.*

data class Post(
    val id: Long,
    val date: Date?,
    val title: String,
    val excerpt: String,
    val authorName: String,
    val authorUrl: Uri,
    val imageUrl: String,
    var subscribersCount: Int?,
    var image: Bitmap? = null
)