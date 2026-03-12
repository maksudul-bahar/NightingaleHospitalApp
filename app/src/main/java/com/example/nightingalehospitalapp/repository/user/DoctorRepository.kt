package com.example.nightingalehospitalapp.repository.user

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.user.Doctor

class DoctorRepository {

    fun addDoctor(doctor: Doctor) {

        val id = FirebaseConfig.doctorsRef.push().key
            ?: throw Exception("Failed to generate doctor ID")

        val updatedDoctor = doctor.copy(doctorId = id)

        FirebaseConfig.doctorsRef
            .child(id)
            .setValue(updatedDoctor)
    }
}