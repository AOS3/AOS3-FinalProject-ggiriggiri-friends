package com.friends.ggiriggiri.data.service

import com.friends.ggiriggiri.data.repository.HomeRepository
import javax.inject.Inject

class HomeService @Inject constructor(private val homeRepository: HomeRepository) {

    fun fetchGroupName(groupId: String, onComplete: (String?) -> Unit) {
        homeRepository.getGroupName(groupId) { groupName ->
            onComplete(groupName ?: "내 그룹") // 기본값 설정
        }
    }

    suspend fun fetchRandomGalleryImages(groupId: String): List<String> {
        return homeRepository.getRandomGalleryImages(groupId)
    }

    fun fetchGroupUserProfiles(groupId: String, onComplete: (List<Pair<String, String>>) -> Unit) {
        homeRepository.getGroupUsers(groupId) { userIds ->
            if (userIds.isNotEmpty()) {
                homeRepository.getUserProfiles(userIds) { profiles ->
                    onComplete(profiles)
                }
            } else {
                onComplete(emptyList()) // 사용자 없음
            }
        }
    }
}