package com.example.nightingalehospitalapp.models.user
import com.example.nightingalehospitalapp.models.enums.UserRole

class Admin(
    id: String,
    name: String,
    email: String,
    phone: String
) : User(id, name, email, phone, UserRole.ADMIN)