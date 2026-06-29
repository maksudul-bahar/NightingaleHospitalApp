package com.example.nightingalehospitalapp.admin.resources

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightingalehospitalapp.models.prescription.Medicine
import com.example.nightingalehospitalapp.ui.components.NightingaleElevatedCard
import com.example.nightingalehospitalapp.ui.components.NightingaleEmptyState
import com.example.nightingalehospitalapp.ui.components.NightingaleListShimmer
import com.example.nightingalehospitalapp.ui.components.NightingalePrimaryButton
import com.example.nightingalehospitalapp.ui.components.NightingaleTextButton
import com.example.nightingalehospitalapp.ui.components.NightingaleTextField
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.admin.resources.MedicineInventoryViewModel

class MedicineInventoryActivity : ComponentActivity() {

    private val viewModel: MedicineInventoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NightingaleHospitalAppTheme {
                MedicineInventoryScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineInventoryScreen(
    viewModel: MedicineInventoryViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var medicineToDelete by remember { mutableStateOf<Medicine?>(null) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Inventory") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Medicine")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is MedicineInventoryViewModel.UiState.Loading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(4) { NightingaleListShimmer() }
                    }
                }
                is MedicineInventoryViewModel.UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MedicineInventoryViewModel.UiState.Loaded -> {
                    if (state.medicines.isEmpty()) {
                        NightingaleEmptyState(
                            title = "No Medicines Found",
                            message = "Your medicine inventory is empty. Add medicines to get started.",
                            icon = Icons.Filled.Info,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.medicines, key = { it.medicineId }) { medicine ->
                                MedicineCard(
                                    medicine = medicine,
                                    onClick = { selectedMedicine = it },
                                    onDelete = { medicineToDelete = it }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddMedicineDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, manufacturer, stock, price ->
                    viewModel.addMedicine(name, manufacturer, stock, price)
                    showAddDialog = false
                }
            )
        }

        selectedMedicine?.let { medicine ->
            UpdateDetailsDialog(
                medicine = medicine,
                onDismiss = { selectedMedicine = null },
                onUpdate = { newStock, newPrice ->
                    viewModel.updateDetails(medicine.medicineId, newStock, newPrice)
                    selectedMedicine = null
                }
            )
        }

        medicineToDelete?.let { medicine ->
            AlertDialog(
                onDismissRequest = { medicineToDelete = null },
                title = { Text("Delete Medicine") },
                text = { Text("Are you sure you want to delete ${medicine.name}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteMedicine(medicine.medicineId)
                            medicineToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { medicineToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun MedicineCard(medicine: Medicine, onClick: (Medicine) -> Unit, onDelete: (Medicine) -> Unit) {
    NightingaleElevatedCard(
        modifier = Modifier.clickable { onClick(medicine) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Inventory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = medicine.manufacturer,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", medicine.price)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stock: ${medicine.stock}",
                    fontSize = 14.sp,
                    color = if (medicine.stock > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onDelete(medicine) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddMedicineDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Int, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Medicine") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NightingaleTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Medicine Name"
                )
                NightingaleTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it },
                    label = "Manufacturer"
                )
                NightingaleTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = "Initial Stock"
                )
                NightingaleTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = "Price ($)"
                )
            }
        },
        confirmButton = {
            NightingalePrimaryButton(
                onClick = {
                    val stock = stockText.toIntOrNull() ?: 0
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    onAdd(name.trim(), manufacturer.trim(), stock, price)
                },
                text = "Add"
            )
        },
        dismissButton = {
            NightingaleTextButton(onClick = onDismiss, text = "Cancel")
        }
    )
}

@Composable
fun UpdateDetailsDialog(
    medicine: Medicine,
    onDismiss: () -> Unit,
    onUpdate: (Int, Double) -> Unit
) {
    var stockText by remember { mutableStateOf(medicine.stock.toString()) }
    var priceText by remember { mutableStateOf(medicine.price.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Details") },
        text = {
            Column {
                Text("Updating details for ${medicine.name}")
                Spacer(modifier = Modifier.height(8.dp))
                NightingaleTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = "New Stock"
                )
                Spacer(modifier = Modifier.height(8.dp))
                NightingaleTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = "New Price ($)"
                )
            }
        },
        confirmButton = {
            NightingalePrimaryButton(
                onClick = {
                    val newStock = stockText.toIntOrNull() ?: 0
                    val newPrice = priceText.toDoubleOrNull() ?: 0.0
                    onUpdate(newStock, newPrice)
                },
                text = "Update"
            )
        },
        dismissButton = {
            NightingaleTextButton(onClick = onDismiss, text = "Cancel")
        }
    )
}
