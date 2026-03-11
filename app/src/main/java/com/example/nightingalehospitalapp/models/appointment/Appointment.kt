package com.example.nightingalehospitalapp.models.appointment
import com.example.nightingalehospitalapp.models.enums.AppointmentStatus

data class Appointment(

    val appointmentId: String = "",
    val doctorId: String = "",
    val patientId: String = "",

    val date: String = "",
    val time: String = "",

    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val notes: String = ""

)