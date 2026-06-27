package com.example.nightingalehospitalapp.repository.prescription

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.prescription.Medicine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MedicineRepository {

    /**
     * Real-time stream of all medicines available in the hospital collection.
     */
    fun observeAllMedicines(): Flow<List<Medicine>> = callbackFlow {
        val registration = FirebaseConfig.medicinesRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Medicine::class.java)?.copy(medicineId = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Add a new medicine entry.
     */
    fun addMedicine(medicine: Medicine, onResult: (Boolean, String?) -> Unit) {
        val id = FirebaseConfig.medicinesRef.document().id
            ?: return onResult(false, "Failed to generate medicine ID")

        val updatedMedicine = medicine.copy(medicineId = id)
        
        FirebaseConfig.medicinesRef
            .document(id)
            .set(updatedMedicine)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    /**
     * Update stock and price of an existing medicine.
     */
    fun updateMedicineDetails(medicineId: String, newStock: Int, newPrice: Double, onResult: (Boolean, String?) -> Unit) {
        if (medicineId.isBlank()) {
            onResult(false, "Invalid Medicine ID")
            return
        }
        
        val updates = mapOf(
            "stock" to newStock,
            "price" to newPrice
        )
        
        FirebaseConfig.medicinesRef
            .document(medicineId)
            .update(updates)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    /**
     * Delete a medicine entry.
     */
    fun deleteMedicine(medicineId: String, onResult: (Boolean, String?) -> Unit) {
        if (medicineId.isBlank()) {
            onResult(false, "Invalid Medicine ID")
            return
        }
        FirebaseConfig.medicinesRef
            .document(medicineId)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
}
