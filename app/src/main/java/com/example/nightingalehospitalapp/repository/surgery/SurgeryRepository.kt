package com.example.nightingalehospitalapp.repository.surgery

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.surgery.SurgeryBooking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SurgeryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /* ------------------ READ (real-time) ------------------ */

    fun observeSurgeriesForPatient(patientId: String): Flow<List<SurgeryBooking>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("surgery_bookings")
            .whereEqualTo("patientId", patientId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(SurgeryBooking::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    fun observeSurgeriesForDoctor(doctorId: String): Flow<List<SurgeryBooking>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("surgery_bookings")
            .whereEqualTo("doctorId", doctorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(SurgeryBooking::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /* ------------------ WRITE ------------------ */

    fun bookSurgery(surgery: SurgeryBooking) {

        val id = FirebaseConfig.surgeryBookingsRef.document().id
            ?: throw Exception("Failed to generate surgery ID")

        val updatedSurgery = surgery.copy(surgeryId = id)

        FirebaseConfig.surgeryBookingsRef
            .document(id)
            .set(updatedSurgery)
    }
}