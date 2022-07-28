package dev.nanday.freshlypressed.models.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nanday.freshlypressed.services.FreshlyPressedDatabase

@Entity(tableName = FreshlyPressedDatabase.BLOG_TABLE_NAME)
data class BlogFromDb(

    @PrimaryKey
    @ColumnInfo(name = FreshlyPressedDatabase.BLOG_URL)
    val url: String,

    @ColumnInfo(name = FreshlyPressedDatabase.BLOG_SUBSCRIBERS_COUNT)
    val subscribersCount: Int
)