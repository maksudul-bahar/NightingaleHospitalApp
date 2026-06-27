package com.example.nightingalehospitalapp.viewmodel.admin.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nightingalehospitalapp.models.prescription.Medicine
import com.example.nightingalehospitalapp.repository.prescription.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MedicineInventoryViewModel : ViewModel() {
    private val repository = MedicineRepository()

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(val medicines: List<Medicine>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        observeMedicines()
    }

    private fun observeMedicines() {
        viewModelScope.launch {
            repository.observeAllMedicines()
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Failed to load medicines") }
                .collectLatest { list ->
                    _uiState.value = UiState.Loaded(list)
                }
        }
    }

    fun addMedicine(name: String, manufacturer: String, stock: Int, price: Double) {
        if (name.isBlank() || manufacturer.isBlank()) {
            _actionMessage.value = "Name and Manufacturer cannot be empty"
            return
        }
        val medicine = Medicine(
            name = name,
            manufacturer = manufacturer,
            stock = stock,
            price = price
        )
        repository.addMedicine(medicine) { success, error ->
            if (success) {
                _actionMessage.value = "Medicine added successfully"
            } else {
                _actionMessage.value = error ?: "Failed to add medicine"
            }
        }
    }

    fun updateDetails(medicineId: String, newStock: Int, newPrice: Double) {
        repository.updateMedicineDetails(medicineId, newStock, newPrice) { success, error ->
            if (success) {
                _actionMessage.value = "Details updated"
            } else {
                _actionMessage.value = error ?: "Failed to update details"
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun deleteMedicine(medicineId: String) {
        repository.deleteMedicine(medicineId) { success, error ->
            if (success) {
                _actionMessage.value = "Medicine deleted successfully"
            } else {
                _actionMessage.value = error ?: "Failed to delete medicine"
            }
        }
    }
}
