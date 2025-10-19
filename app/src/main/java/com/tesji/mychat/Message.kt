package com.tesji.mychat

data class Message(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = 0
)