package com.example.nightingalehospitalapp.models.notification

data class Notification(

    val notificationId: String = "",
    val userId: String = "",

    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    val isRead: Boolean = false

)