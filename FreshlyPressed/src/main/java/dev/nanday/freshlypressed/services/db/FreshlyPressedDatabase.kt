package dev.nanday.freshlypressed.services

import androidx.room.*
import dev.nanday.freshlypressed.models.dtos.BlogFromDb
import dev.nanday.freshlypressed.models.dtos.PostFromDb
import dev.nanday.freshlypressed.services.FreshlyPressedDatabase.Companion.BLOG_TABLE_NAME
import dev.nanday.freshlypressed.services.FreshlyPressedDatabase.Companion.BLOG_URL
import dev.nanday.freshlypressed.services.FreshlyPressedDatabase.Companion.POST_ID
import dev.nanday.freshlypressed.services.FreshlyPressedDatabase.Companion.POST_TABLE_NAME

@Database(
    version = 1,
    entities = [ PostFromDb::class, BlogFromDb::class ]
)
abstract class FreshlyPressedDatabase : RoomDatabase() {

    abstract fun dao(): FreshlyPressedDao

    companion object {
        // Database table and column names //
        const val POST_TABLE_NAME = "posts"
        const val POST_ID = "id"
        const val POST_TITLE = "title"
        const val POST_EXCERPT = "excerpt"
        const val POST_AUTHOR = "author"
        const val POST_DATE = "date"
        const val POST_AUTHOR_URL = "author_url"
        const val POST_URI = "uri"
        const val POST_IMAGE_URL = "image_url"
        const val POST_SUBSCRIBERS_COUNT = "subscribers_count"
        const val POST_IMAGE = "image"

        const val BLOG_TABLE_NAME = "blogs"
        const val BLOG_URL = "url"
        const val BLOG_SUBSCRIBERS_COUNT = "subscribersCount"
    }
}

@Dao
interface FreshlyPressedDao {

    @Query("DELETE FROM $POST_TABLE_NAME")
    suspend fun deleteAllPosts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostFromDb>) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePost(post: PostFromDb)

    @Query("SELECT * FROM $POST_TABLE_NAME")
    suspend fun getAllPosts() : List<PostFromDb>

    @Query("SELECT * FROM $POST_TABLE_NAME WHERE $POST_ID = :postId")
    suspend fun getPostById(postId: Long): PostFromDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBlog(blog: BlogFromDb)

    @Query("SELECT * FROM $BLOG_TABLE_NAME WHERE $BLOG_URL = :blogUrl")
    suspend fun getBlog(blogUrl: String): BlogFromDb?
}