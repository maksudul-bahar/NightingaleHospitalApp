package com.example.nightingalehospitalapp.models.diagnostic

import com.example.nightingalehospitalapp.models.enums.TestStatus

data class TestBooking(

    val bookingId: String = "",
    val testId: String = "",
    val patientId: String = "",
    val doctorId: String = "",

    val date: String = "",
    val status: TestStatus = TestStatus.SCHEDULED

)