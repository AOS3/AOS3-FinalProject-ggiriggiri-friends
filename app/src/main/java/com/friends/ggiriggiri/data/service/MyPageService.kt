package com.friends.ggiriggiri.data.service


import android.util.Log
import com.friends.ggiriggiri.data.model.GroupModel2
import com.friends.ggiriggiri.data.repository.MyPageRepository
import com.friends.ggiriggiri.ui.start.register.UserModel
import javax.inject.Inject

class MyPageService @Inject constructor(
    val repository: MyPageRepository
) {

    // 프로필 이미지 변경
    suspend fun updateProfileImage(userDocumentId: String, newProfileImageUrl: String) {
        repository.updateUserProfileImage(userDocumentId, newProfileImageUrl)
    }

    // 사용자 문서 아이디를 통해 사용자 정보를 가져온다.
    suspend fun selectUserDataByUserDocumentIdOne(userDocumentId:String) : UserModel {
        val userVO = repository.selectUserDataByUserDocumentIdOne(userDocumentId)
        val userModel = userVO.toUserModel(userDocumentId)
        return userModel
    }

    // 소셜 로그인 여부 확인
    suspend fun isSocialLoginUser(userDocumentId: String): Boolean {
        return repository.isSocialLoginUser(userDocumentId)
    }

    // 그룹 문서 아이디를 통해 그룹 정보를 가져온다.
    suspend fun selectGroupDataByGroupDocumentIdOne(groupDocumentId:String) : GroupModel2 {
        Log.d("selectGroupDataByGroupDocumentIdOne", "groupDocumentId: $groupDocumentId")

        val groupVO = repository.selectGroupDataByGroupDocumentIdOne(groupDocumentId)
        val groupModel = groupVO.toGroupModel(groupDocumentId)

        return groupModel
    }

    // 그룹 문서 아이디를 통해 그룹이름 를 가져온다.
    suspend fun getGroupName(groupDocumentId: String): String {
        Log.d("getGroupName", "groupDocumentId: $groupDocumentId")

        return repository.selectGroupNameByGroupDocumentId(groupDocumentId)
    }

    // 그룹 문서 아이디를 통해 그룹코드를 가져온다.
    suspend fun getGroupCode(groupDocumentId: String): String {
        return repository.selectGroupCodeByGroupDocumentId(groupDocumentId)
    }

    // 그룹 문서 아이디를 통해 그룹 비밀번호를 가져온다.
    suspend fun getGroupPw(groupDocumentId: String): String {
        return repository.selectGroupPwByGroupDocumentId(groupDocumentId)
    }


    // 사용자 비밀번호 수정
    suspend fun updateUserPw(userModel: UserModel){
        val userVO = userModel.toUserVO()
        repository.updateUserData(userVO, userModel.userDocumentId)
    }

    // 그룹 비밀번호 수정
    suspend fun updateGroupPw(groupModel: GroupModel2){
        val groupVO = groupModel.toGroupVO()
        repository.updateGroupPw(groupVO, groupModel.groupDocumentId)
    }

    // 그룹명 수정
    suspend fun updateGroupName(groupModel: GroupModel2){
        val groupVO = groupModel.toGroupVO()
        repository.updateGroupName(groupVO, groupModel.groupDocumentId)
    }

    // 그룹 탈퇴
    suspend fun exitGroup(userDocumentId: String) {
        repository.exitGroup(userDocumentId)
    }
}