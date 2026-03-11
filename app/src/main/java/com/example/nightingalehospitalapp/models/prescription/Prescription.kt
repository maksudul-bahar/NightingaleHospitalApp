package com.example.nightingalehospitalapp.models.prescription

data class Prescription(

    val prescriptionId: String = "",
    val appointmentId: String = "",
    val doctorId: String = "",
    val patientId: String = "",

    val diagnosis: String = "",
    val date: String = ""

)