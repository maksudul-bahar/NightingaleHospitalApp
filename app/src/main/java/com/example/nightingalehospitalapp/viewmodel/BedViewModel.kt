package com.example.nightingalehospitalapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.hospital.Bed
import com.example.nightingalehospitalapp.models.enums.BedStatus
import com.example.nightingalehospitalapp.models.user.User
import com.example.nightingalehospitalapp.repository.bed.BedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BedViewModel : ViewModel() {

    private val repository = BedRepository()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Loaded(val beds: List<Bed>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _bedsState = MutableStateFlow<UiState>(UiState.Idle)
    val bedsState: StateFlow<UiState> = _bedsState.asStateFlow()

    private val _patients = MutableStateFlow<List<User>>(emptyList())
    val patients: StateFlow<List<User>> = _patients.asStateFlow()

    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult.asStateFlow()

    init {
        observeBeds()
        fetchPatients()
    }

    private fun observeBeds() {
        _bedsState.value = UiState.Loading
        viewModelScope.launch {
            repository.seedDemoBedsIfEmpty()
            repository.observeBeds()
                .catch { e ->
                    _bedsState.value = UiState.Error(e.message ?: "Failed to load beds")
                }
                .collectLatest { list ->
                    _bedsState.value = UiState.Loaded(list)
                }
        }
    }

    fun fetchPatients() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseConfig.usersRef
                    .whereEqualTo("role", "PATIENT")
                    .get()
                    .await()
                val list = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                _patients.value = list
            } catch (e: Exception) {
                // Fallback / ignore
            }
        }
    }

    fun addBed(roomNumber: String, ward: String, bedType: String, status: BedStatus) {
        viewModelScope.launch {
            val bed = Bed(
                roomNumber = roomNumber,
                ward = ward,
                bedType = bedType,
                status = status
            )
            val result = repository.addBed(bed)
            _operationResult.value = result.fold(
                onSuccess = { "Bed added successfully" },
                onFailure = { it.message ?: "Failed to add bed" }
            )
        }
    }

    fun updateBed(bed: Bed) {
        viewModelScope.launch {
            val result = repository.updateBed(bed)
            _operationResult.value = result.fold(
                onSuccess = { "Bed updated successfully" },
                onFailure = { it.message ?: "Failed to update bed" }
            )
        }
    }

    fun deleteBed(bedId: String) {
        viewModelScope.launch {
            val result = repository.deleteBed(bedId)
            _operationResult.value = result.fold(
                onSuccess = { "Bed deleted successfully" },
                onFailure = { it.message ?: "Failed to delete bed" }
            )
        }
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }
}
