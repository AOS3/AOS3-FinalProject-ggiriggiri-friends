package com.friends.ggiriggiri.data.vo

import com.friends.ggiriggiri.data.model.GroupModel2
import com.friends.ggiriggiri.util.GroupState

class GroupVO {
    // 그룹 생성시간
    var groupCreateTime: Long = 0L
    // 그룹 상태
    var groupState = 1 // 1: 활성화, 2: 비활성화 (기본값: 1)
    // 그룹 이름
    var groupName: String = ""
    // 그룹 코드
    var groupCode: String = ""
    // 그룹 비밀번호
    var groupPw: String = ""
    // 유저리스트 DocumnetID
    var groupUserDocumentID: List<String> = listOf()
    // 요청() 문서 아이디 리스트
    var groupRequestDocumentID: List<String> = listOf()
    // 질문() 문서 아이디 리스트
    var groupQuestionDocumentID: List<String> = listOf()
    // 그룹 갤러리(이미지들)
    var groupGallery: List<String> = listOf()

    fun toGroupModel(groupDocumentId:String) : GroupModel2 {
        val groupModel = GroupModel2()

        groupModel.groupDocumentId = groupDocumentId
        groupModel.groupPw = groupPw
        groupModel.groupName = groupName
        groupModel.groupCode = groupCode

        when(groupState){
            GroupState.NORMAL.num -> groupModel.groupState = GroupState.NORMAL
            GroupState.WITHDRAW.num -> groupModel.groupState = GroupState.WITHDRAW
        }

        groupModel.groupCreateTime = groupCreateTime
        groupModel.groupUserDocumentID = groupUserDocumentID.toList()
        groupModel.groupRequestDocumentID = groupRequestDocumentID.toList()
        groupModel.groupQuestionDocumentID = groupQuestionDocumentID.toList()
        groupModel.groupGallery = groupGallery.toList()

        return groupModel
    }
}