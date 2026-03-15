package com.example.nightingalehospitalapp.models.user

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "PATIENT",
    val approved: Boolean = false
)