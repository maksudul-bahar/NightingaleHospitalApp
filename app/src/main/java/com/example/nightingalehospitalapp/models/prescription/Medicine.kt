package com.example.nightingalehospitalapp.models.prescription

data class Medicine(

    val medicineId: String = "",
    val name: String = "",
    val description: String = "",
    val stockQuantity: Int = 0,
    val price: Double = 0.0

)