package com.example.nightingalehospitalapp.repository.appointment

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.appointment.Appointment

class AppointmentRepository {

    fun bookAppointment(appointment: Appointment) {

        val id = FirebaseConfig.appointmentsRef.push().key
            ?: throw Exception("Failed to generate appointment ID")

        val updatedAppointment = appointment.copy(appointmentId = id)

        FirebaseConfig.appointmentsRef
            .child(id)
            .setValue(updatedAppointment)
    }
}