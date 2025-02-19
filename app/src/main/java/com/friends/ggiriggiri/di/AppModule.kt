package com.friends.ggiriggiri.di

import android.content.Context
import android.content.SharedPreferences
import com.friends.ggiriggiri.data.repository.GoogleLoginRepository
import com.friends.ggiriggiri.data.service.GoogleLoginService
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

    @Provides
    @Singleton
    fun provideGoogleLoginService(googleLoginRepository: GoogleLoginRepository): GoogleLoginService {
        return GoogleLoginService(googleLoginRepository)
    }
}