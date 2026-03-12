package com.example.nightingalehospitalapp.repository.user

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.user.Patient

class PatientRepository {

    fun registerPatient(patient: Patient) {
        val id = FirebaseConfig.patientsRef.push().key!!
        
        // Create a copy of the patient with the new ID
        val updatedPatient = patient.copy(patientId = id)

        FirebaseConfig.patientsRef.child(id).setValue(updatedPatient)
    }
}
