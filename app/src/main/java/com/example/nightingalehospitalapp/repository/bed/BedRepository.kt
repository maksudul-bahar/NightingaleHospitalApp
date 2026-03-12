package com.example.nightingalehospitalapp.repository.bed

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.hospital.Bed

class BedRepository {

    fun addBed(bed: Bed) {

        val id = FirebaseConfig.bedsRef.push().key
            ?: throw Exception("Failed to generate bed ID")

        val updatedBed = bed.copy(bedId = id)

        FirebaseConfig.bedsRef
            .child(id)
            .setValue(updatedBed)
    }
}