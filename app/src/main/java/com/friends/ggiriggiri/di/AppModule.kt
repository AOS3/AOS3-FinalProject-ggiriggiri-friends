package com.friends.ggiriggiri.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.friends.ggiriggiri.App
import com.friends.ggiriggiri.data.repository.GoogleLoginRepository
import com.friends.ggiriggiri.data.repository.KakaoLoginRepository
import com.friends.ggiriggiri.data.repository.NaverLoginRepository
import com.friends.ggiriggiri.data.repository.RequestDetailRepository
import com.friends.ggiriggiri.data.repository.RequestListRepository
import com.friends.ggiriggiri.data.service.GoogleLoginService
import com.friends.ggiriggiri.data.service.KakaoLoginService
import com.friends.ggiriggiri.data.service.NaverLoginService
import com.friends.ggiriggiri.data.service.RequestDetailService
import com.friends.ggiriggiri.data.service.RequestListService
import com.friends.ggiriggiri.ui.notification.NotificationDao
import com.friends.ggiriggiri.ui.notification.NotificationDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("GGiriggiriPrefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideGoogleLoginRepository(firebaseAuth: FirebaseAuth): GoogleLoginRepository {
        return GoogleLoginRepository(firebaseAuth)
    }

//    @Provides
//    @Singleton
//    fun provideGoogleLoginService(googleLoginRepository: GoogleLoginRepository): GoogleLoginService {
//        return GoogleLoginService(googleLoginRepository)
//    }
    @Provides
    @Singleton
    fun provideRequestListRepository(firestore: FirebaseFirestore): RequestListRepository {
        return RequestListRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideRequestListService(repository: RequestListRepository): RequestListService {
        return RequestListService(repository)
    }

    @Provides
    @Singleton
    fun provideRequestDetailRepository(firestore: FirebaseFirestore): RequestDetailRepository {
        return RequestDetailRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideRequestDetailService(repository: RequestDetailRepository): RequestDetailService {
        return RequestDetailService(repository)
    }

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext context: Context): App {
        return context.applicationContext as App
    }

    @Provides
    @Singleton
    fun provideKakaoLoginRepository(): KakaoLoginRepository {
        return KakaoLoginRepository()
    }

    @Provides
    @Singleton
    fun provideKakaoLoginService(repository: KakaoLoginRepository, sharedPreferences: SharedPreferences): KakaoLoginService {
        return KakaoLoginService(repository, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideGoogleLoginService(
        googleLoginRepository: GoogleLoginRepository,
        sharedPreferences: SharedPreferences
    ): GoogleLoginService {
        return GoogleLoginService(googleLoginRepository, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideNaverLoginService(
        repository: NaverLoginRepository,
        sharedPreferences: SharedPreferences
    ): NaverLoginService {
        return NaverLoginService(repository, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideNaverLoginRepository(): NaverLoginRepository {
        return NaverLoginRepository()
    }

    @Provides
    @Singleton
    fun provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): NotificationDatabase {
        return Room.databaseBuilder(
            context,
            NotificationDatabase::class.java,
            "notification_database"
        ).build()
    }

    @Provides
    fun provideNotificationDao(database: NotificationDatabase): NotificationDao {
        return database.notificationDao()
    }
}