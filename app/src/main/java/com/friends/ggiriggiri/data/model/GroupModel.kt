package com.friends.ggiriggiri.data.model

data class GroupModel (
    // 그룹 생성시간
    var groupCreateTime: Long = 0L,
    // 그룹 상태
    var groupState: Int = 1, // 1: 활성화, 2: 비활성화 (기본값: 1)
    // 그룹 이름
    var groupName: String = "",
    // 그룹 코드
    var groupCode: String = "",
    // 그룹 비밀번호
    var groupPw: String = "",
    // 유저리스트 DocumnetID
    var groupUserDocumentID: List<String> = listOf(),
    // 요청() 문서 아이디 리스트
    var groupRequestDocumentID: String = "",
    // 질문() 문서 아이디 리스트
    var groupQuestionDocumentID: String = "",
    // 그룹 갤러리(이미지들)
    var groupGallery: String = ""
)