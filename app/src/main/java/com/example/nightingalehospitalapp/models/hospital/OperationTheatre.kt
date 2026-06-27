package com.example.nightingalehospitalapp.models.hospital

data class OperationTheatre(
    val otId: String = "",
    val roomNumber: String = "",
    val floor: String = "",
    val status: String = "FREE",
    val capacity: Int = 1,
    val assignedDoctorId: String? = null,
    val assignedDoctorName: String? = null,
    val assignedDepartmentId: String? = null,
    val assignedDepartmentName: String? = null,
    val schedule: String? = null
)