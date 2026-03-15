package com.example.nightingalehospitalapp.models.user

import com.example.nightingalehospitalapp.models.enums.UserRole

// Admin data class describing administrative user details. We treat it separately
// instead of subclassing the final User data class.
data class Admin(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.ADMIN
)