package com.friends.ggiriggiri.util.tools

import java.text.SimpleDateFormat
import java.util.Locale

class date {
    fun date(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 기준 날짜 (예: 2024-01-01)
        val baseDate = dateFormat.parse("2024-12-25")!!.time

        // 현재 시간 (밀리초)
        val currentTime = System.currentTimeMillis()

        // 경과한 일수 계산 (밀리초 → 일)
        val elapsedDays = (currentTime - baseDate) / (1000 * 60 * 60 * 24)

        println("기준 날짜로부터 경과한 일수: $elapsedDays 일")
    }
}