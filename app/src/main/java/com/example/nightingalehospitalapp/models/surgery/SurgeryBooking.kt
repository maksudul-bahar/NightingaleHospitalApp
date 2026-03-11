package com.example.nightingalehospitalapp.models.surgery

import com.example.nightingalehospitalapp.models.enums.SurgeryStatus

data class SurgeryBooking(

    val surgeryId: String = "",
    val otId: String = "",

    val doctorId: String = "",
    val patientId: String = "",

    val surgeryType: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",

    val status: SurgeryStatus = SurgeryStatus.AVAILABLE

)