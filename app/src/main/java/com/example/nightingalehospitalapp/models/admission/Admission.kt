package com.example.nightingalehospitalapp.models.admission
import com.example.nightingalehospitalapp.models.enums.AdmissionStatus


data class Admission(

    var id: String = "",

    var patientId: String = "",

    var doctorId: String = "",

    var departmentId: String = "",

    var bedId: String = "",

    var reason: String = "",

    var admissionDate: Long = 0,

    var dischargeDate: Long? = null,

    var status: AdmissionStatus = AdmissionStatus.ADMITTED

)