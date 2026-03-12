package com.example.nightingalehospitalapp.models.user

import com.example.nightingalehospitalapp.models.enums.UserRole

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.PATIENT,
    val approved: Boolean = false
)