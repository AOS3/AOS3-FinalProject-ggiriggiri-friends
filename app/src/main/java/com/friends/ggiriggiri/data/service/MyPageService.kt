package com.friends.ggiriggiri.data.service

import android.net.Uri
import com.friends.ggiriggiri.data.model.GroupModel
import com.friends.ggiriggiri.data.repository.MyPageRepository
import com.friends.ggiriggiri.ui.first.register.UserModel
import com.friends.ggiriggiri.ui.first.register.UserRepository
import javax.inject.Inject

class MyPageService @Inject constructor(
    val repository: MyPageRepository
) {

    // 프로필 이미지 가져오기
    suspend fun getUserProfileImage(uid: String): String? {
        return repository.getUserProfileImage(uid)
    }

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

    // 그룹 문서 아이디를 통해 그룹 정보를 가져온다.
    suspend fun selectGroupDataByGroupDocumentIdOne(groupDocumentId:String) : GroupModel {
        val groupVO = repository.selectGroupDataByGroupDocumentIdOne(groupDocumentId)
        val groupModel = groupVO.toGroupModel(groupDocumentId)
        return groupModel
    }

    // 그룹 문서 아이디를 통해 그룹이름 를 가져온다.
    suspend fun getGroupName(groupDocumentId: String): String {
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
    suspend fun updateGroupPw(groupModel: GroupModel){
        val groupVO = groupModel.toGroupVO()
        repository.updateGroupPw(groupVO, groupModel.groupDocumentId)
    }

    // 그룹명 수정
    suspend fun updateGroupName(groupModel: GroupModel){
        val groupVO = groupModel.toGroupVO()
        repository.updateGroupName(groupVO, groupModel.groupDocumentId)
    }

    // 그룹 탈퇴
    suspend fun exitGroup(userDocumentId: String) {
        repository.exitGroup(userDocumentId)
    }
}