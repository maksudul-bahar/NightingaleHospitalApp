package com.example.nightingalehospitalapp.models.user

import com.example.nightingalehospitalapp.models.enums.UserRole

open class User(

    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.PATIENT

)