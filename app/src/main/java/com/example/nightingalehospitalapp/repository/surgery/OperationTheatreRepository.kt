package com.example.nightingalehospitalapp.repository.surgery

import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.hospital.OperationTheatre
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OperationTheatreRepository {

    fun observeTheatres(): Flow<List<OperationTheatre>> = callbackFlow {
        val reg: ListenerRegistration = FirebaseConfig.operationTheatresRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(OperationTheatre::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun addTheatre(theatre: OperationTheatre): Result<Unit> {
        return try {
            val id = if (theatre.otId.isBlank()) FirebaseConfig.operationTheatresRef.document().id else theatre.otId
            val updatedTheatre = theatre.copy(otId = id)
            FirebaseConfig.operationTheatresRef.document(id).set(updatedTheatre).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTheatre(theatre: OperationTheatre): Result<Unit> {
        return try {
            FirebaseConfig.operationTheatresRef.document(theatre.otId).set(theatre).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTheatre(theatreId: String): Result<Unit> {
        return try {
            FirebaseConfig.operationTheatresRef.document(theatreId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seedDemoTheatresIfEmpty(): Result<Unit> {
        return try {
            val existing = FirebaseConfig.operationTheatresRef.get().await()
            if (!existing.isEmpty) return Result.success(Unit)

            val demos = listOf(
                OperationTheatre(otId = "", roomNumber = "OT-1", floor = "1st Floor", status = "FREE", capacity = 1),
                OperationTheatre(otId = "", roomNumber = "OT-2", floor = "1st Floor", status = "IN_USE", capacity = 2, assignedDoctorName = "Dr. Amit Roy", schedule = "14:00 - 16:00"),
                OperationTheatre(otId = "", roomNumber = "OT-3", floor = "2nd Floor", status = "CLEANING", capacity = 1)
            )
            demos.forEach { addTheatre(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
