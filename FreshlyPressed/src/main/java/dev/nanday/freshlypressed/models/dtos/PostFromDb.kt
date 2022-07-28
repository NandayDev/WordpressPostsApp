package dev.nanday.freshlypressed.models.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nanday.freshlypressed.services.FreshlyPressedDatabase

@Entity(tableName = FreshlyPressedDatabase.POST_TABLE_NAME)
data class PostFromDb(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FreshlyPressedDatabase.POST_ID)
    var id: Long? = null,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_TITLE)
    val title: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_EXCERPT)
    val excerpt: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_AUTHOR)
    val authorName: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_DATE)
    val date: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_AUTHOR_URL)
    val authorUrl: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_URI)
    val uri: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_IMAGE_URL)
    val imageUrl: String,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_SUBSCRIBERS_COUNT)
    val subscribersCount: Int?,

    @ColumnInfo(name = FreshlyPressedDatabase.POST_IMAGE)
    var imagePath: String?
)