package dev.nanday.freshlypressed

import android.app.Application
import android.content.Context
import androidx.room.Room
import dev.nanday.freshlypressed.services.*
import dev.nanday.freshlypressed.ui.PostListActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FreshlyPressedApplication : Application() {

    companion object {
        const val API_ENDPOINT = "https://public-api.wordpress.com/rest/v1.1/sites/"
    }

    override fun onCreate() {
        super.onCreate()

        // Dependency injection via Koin //
        startKoin {
            androidContext(this@FreshlyPressedApplication)
            modules(appModule)
        }
    }

    /**
     * Koin D.I. module
     */
    private val appModule = module {
        fun provideDatabase(context: Context) : FreshlyPressedDatabase = Room.databaseBuilder(context, FreshlyPressedDatabase::class.java, "db").build()
        fun provideDao(database: FreshlyPressedDatabase) : FreshlyPressedDao = database.dao()

        single { provideDatabase(androidContext()) }
        single { provideDao(get()) }

        // API services //
        factory { Retrofit.Builder()
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java) } bind PostApiService::class

        single { DtoMapper() }
        single { PostRepositoryImpl(androidContext(), get(), get(), get()) } bind PostRepository::class
        single { BlogRepositoryImpl(get(), get()) } bind BlogRepository::class

        viewModel { PostListActivityViewModel(androidContext(), get(), get()) }
    }
}