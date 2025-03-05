package com.friends.ggiriggiri.data.repository

import com.friends.ggiriggiri.data.vo.GroupVO
import com.friends.ggiriggiri.ui.first.register.UserVO
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyPageRepository @Inject constructor(
) {

    // 프로필 이미지 URL 업데이트 (Storage를 사용하지 않고 Firestore 필드만 수정)
    suspend fun updateUserProfileImage(uid: String, newImageUrl: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("UserData")
            .document(uid)
            .update("userProfileImage", newImageUrl)
            .await()
    }

    // 사용자 문서 아이디를 통해 사용자 정보를 가져온다.
    suspend fun selectUserDataByUserDocumentIdOne(userDocumentId:String) : UserVO {
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("UserData")
        val result = collectionReference.document(userDocumentId).get().await()
        val userVO = result.toObject(UserVO::class.java)!!
        return userVO
    }

    // 소셜 로그인 여부 확인
    suspend fun isSocialLoginUser(userDocumentId: String): Boolean {
        val firestore = FirebaseFirestore.getInstance()
        val userSnapshot = firestore.collection("UserData").document(userDocumentId).get().await()
        val userSocialLogin = userSnapshot.getLong("userSocialLogin") ?: 1L
        return userSocialLogin != 1L // 1이 아니면 소셜 로그인
    }

    // 그룹 문서 아이디를 통해 그룹 정보를 가져온다.
    suspend fun selectGroupDataByGroupDocumentIdOne(groupDocumentId: String) : GroupVO {
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("GroupData")
        val result = collectionReference.document(groupDocumentId).get().await()
        val groupVO = result.toObject(GroupVO::class.java)!!
        return groupVO
    }

    // 그룹명 가져오기
    suspend fun selectGroupNameByGroupDocumentId(groupDocumentId: String): String {
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("GroupData")
        val result = collectionReference.document(groupDocumentId).get().await()
        return result.getString("groupName") ?: "알 수 없음"
    }

    //그룹 코드 가져오기
    suspend fun selectGroupCodeByGroupDocumentId(groupDocumentId: String): String {
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("GroupData")
        val result = collectionReference.document(groupDocumentId).get().await()
        return result.getString("groupCode") ?: "알 수 없음"
    }

    // 그룹 비밀번호 가져오기
    suspend fun selectGroupPwByGroupDocumentId(groupDocumentId: String): String {
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("GroupData")
        val result = collectionReference.document(groupDocumentId).get().await()
        return result.getString("groupPw") ?: "알 수 없음"
    }

    // 사용자 비밀번호 수정
    suspend fun updateUserData(userVO: UserVO, userDocumentId: String){
        // 수정할 데이터를 담을 맵
        val userMap = mapOf(
            "userPw" to userVO.userPw
        )
        // 수정할 문서에 접근할 수 있는 객체를 가져온다.
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("UserData")
        val documentReference = collectionReference.document(userDocumentId)
        documentReference.update(userMap).await()
    }

    // 그룹 비밀번호 수정
    suspend fun updateGroupPw(groupVO: GroupVO, groupDocumentId: String){
        // 수정할 데이터를 담을 맵
         val groupMap = mapOf(
             "groupPw" to groupVO.groupPw
         )
        // 수정할 문서에 접근할 수있는 객체를 가져온다.
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("GroupData")
        val documentReference = collectionReference.document(groupDocumentId)
        documentReference.update(groupMap).await()
    }

    // 그룹 비밀번호 수정
    suspend fun updateGroupName(groupVO: GroupVO, groupDocumentId: String){
        // 수정할 데이터를 담을 맵
        val groupMap = mapOf(
            "groupName" to groupVO.groupName
        )
        // 수정할 문서에 접근할 수있는 객체를 가져온다.
        val firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("GroupData")
        val documentReference = collectionReference.document(groupDocumentId)
        documentReference.update(groupMap).await()
    }

    // 그룹 탈퇴
    suspend fun exitGroup(userDocumentId: String) {
        val firestore = FirebaseFirestore.getInstance()

        val userSnapshot = firestore.collection("UserData")
            .document(userDocumentId)
            .get()
            .await()

        // 현재 유저가 속한 그룹 ID 가져오기
        val groupDocumentId = userSnapshot.getString("userGroupDocumentID") ?: ""

        // userGroupDocumentID 필드를 완전히 삭제
        firestore.collection("UserData")
            .document(userDocumentId)
            .update("userGroupDocumentID", FieldValue.delete())
            .await()

        // GroupData 컬렉션에서 groupUserDocumentID 배열에서 유저 ID 삭제
        if (groupDocumentId.isNotEmpty()) {
            firestore.collection("GroupData")
                .document(groupDocumentId)
                .update("groupUserDocumentID", FieldValue.arrayRemove(userDocumentId))
                .await()
        }
    }
}