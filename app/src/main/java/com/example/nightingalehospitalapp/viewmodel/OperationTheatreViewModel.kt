package com.example.nightingalehospitalapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.hospital.OperationTheatre
import com.example.nightingalehospitalapp.models.user.User
import com.example.nightingalehospitalapp.repository.surgery.OperationTheatreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OperationTheatreViewModel : ViewModel() {

    private val repository = OperationTheatreRepository()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Loaded(val theatres: List<OperationTheatre>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _theatresState = MutableStateFlow<UiState>(UiState.Idle)
    val theatresState: StateFlow<UiState> = _theatresState.asStateFlow()

    private val _doctors = MutableStateFlow<List<User>>(emptyList())
    val doctors: StateFlow<List<User>> = _doctors.asStateFlow()

    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult.asStateFlow()

    init {
        observeTheatres()
        fetchDoctors()
    }

    private fun observeTheatres() {
        _theatresState.value = UiState.Loading
        viewModelScope.launch {
            repository.seedDemoTheatresIfEmpty()
            repository.observeTheatres()
                .catch { e ->
                    _theatresState.value = UiState.Error(e.message ?: "Failed to load theatres")
                }
                .collectLatest { list ->
                    _theatresState.value = UiState.Loaded(list)
                }
        }
    }

    fun fetchDoctors() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseConfig.usersRef
                    .whereEqualTo("role", "DOCTOR")
                    .get()
                    .await()
                val list = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                _doctors.value = list
            } catch (e: Exception) {
                // Fallback / ignore
            }
        }
    }

    fun addTheatre(roomNumber: String, floor: String, status: String, capacity: Int, schedule: String?, docId: String?, docName: String?) {
        viewModelScope.launch {
            val theatre = OperationTheatre(
                roomNumber = roomNumber,
                floor = floor,
                status = status,
                capacity = capacity,
                schedule = schedule,
                assignedDoctorId = docId,
                assignedDoctorName = docName
            )
            val result = repository.addTheatre(theatre)
            _operationResult.value = result.fold(
                onSuccess = { "Theatre added successfully" },
                onFailure = { it.message ?: "Failed to add theatre" }
            )
        }
    }

    fun updateTheatre(theatre: OperationTheatre) {
        viewModelScope.launch {
            val result = repository.updateTheatre(theatre)
            _operationResult.value = result.fold(
                onSuccess = { "Theatre updated successfully" },
                onFailure = { it.message ?: "Failed to update theatre" }
            )
        }
    }

    fun deleteTheatre(theatreId: String) {
        viewModelScope.launch {
            val result = repository.deleteTheatre(theatreId)
            _operationResult.value = result.fold(
                onSuccess = { "Theatre deleted successfully" },
                onFailure = { it.message ?: "Failed to delete theatre" }
            )
        }
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }
}
