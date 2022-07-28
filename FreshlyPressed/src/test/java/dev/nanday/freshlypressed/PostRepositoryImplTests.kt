package dev.nanday.freshlypressed

import android.content.Context
import dev.nanday.freshlypressed.models.dtos.PostResponse
import dev.nanday.freshlypressed.services.DtoMapper
import dev.nanday.freshlypressed.services.FreshlyPressedDao
import dev.nanday.freshlypressed.services.PostApiService
import dev.nanday.freshlypressed.services.PostRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import retrofit2.HttpException

class PostRepositoryImplTests {

    private val contextMock = Mockito.mock(Context::class.java)
    private val daoMock = Mockito.mock(FreshlyPressedDao::class.java)
    private val apiServiceMock = Mockito.mock(PostApiService::class.java)
    private val dtoMapperMock = Mockito.mock(DtoMapper::class.java)
    private var postRepository: PostRepositoryImpl = PostRepositoryImpl(
        contextMock,
        daoMock,
        apiServiceMock,
        dtoMapperMock
    )

    @Test
    fun `Ensure posts are loaded from Dao (cache) when connectivity is absent`(): Unit = runBlocking {
        Mockito.`when`(apiServiceMock.getPosts())
            .thenThrow(Mockito.mock(HttpException::class.java))

        Mockito.`when`(daoMock.getAllPosts())
            .thenReturn(listOf())

        postRepository.loadPosts()

        Mockito
            .verify(daoMock, Mockito.times(1))
            .getAllPosts()
    }

    @Test
    fun `Ensure posts are NOT loaded from Dao (cache) when API answers correctly`(): Unit = runBlocking {
        Mockito.`when`(apiServiceMock.getPosts())
            .thenReturn(PostResponse(listOf()))

        postRepository.loadPosts()

        Mockito
            .verify(daoMock, Mockito.never())
            .getAllPosts()
    }
}