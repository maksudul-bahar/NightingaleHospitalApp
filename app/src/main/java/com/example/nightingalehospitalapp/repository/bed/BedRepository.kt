package com.example.nightingalehospitalapp.repository.bed

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.hospital.Bed
import com.example.nightingalehospitalapp.models.enums.BedStatus
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BedRepository {

    fun observeBeds(): Flow<List<Bed>> = callbackFlow {
        val reg: ListenerRegistration = FirebaseConfig.bedsRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(Bed::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun addBed(bed: Bed): Result<Unit> {
        return try {
            val id = if (bed.bedId.isBlank()) FirebaseConfig.bedsRef.document().id else bed.bedId
            val updatedBed = bed.copy(bedId = id)
            FirebaseConfig.bedsRef.document(id).set(updatedBed).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBed(bed: Bed): Result<Unit> {
        return try {
            FirebaseConfig.bedsRef.document(bed.bedId).set(bed).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBed(bedId: String): Result<Unit> {
        return try {
            FirebaseConfig.bedsRef.document(bedId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seedDemoBedsIfEmpty(): Result<Unit> {
        return try {
            val existing = FirebaseConfig.bedsRef.get().await()
            if (!existing.isEmpty) return Result.success(Unit)

            val demos = listOf(
                Bed(bedId = "", roomNumber = "101", ward = "General Ward A", status = BedStatus.AVAILABLE, bedType = "General"),
                Bed(bedId = "", roomNumber = "102", ward = "General Ward A", status = BedStatus.OCCUPIED, bedType = "General", patientId = "demo-p1", patientName = "John Doe"),
                Bed(bedId = "", roomNumber = "ICU-01", ward = "ICU", status = BedStatus.AVAILABLE, bedType = "ICU"),
                Bed(bedId = "", roomNumber = "201", ward = "Semi-Private Ward B", status = BedStatus.MAINTENANCE, bedType = "Semi-Private")
            )
            demos.forEach { addBed(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}