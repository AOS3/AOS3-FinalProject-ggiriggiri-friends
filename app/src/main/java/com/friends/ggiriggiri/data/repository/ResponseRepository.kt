package com.friends.ggiriggiri.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

}
