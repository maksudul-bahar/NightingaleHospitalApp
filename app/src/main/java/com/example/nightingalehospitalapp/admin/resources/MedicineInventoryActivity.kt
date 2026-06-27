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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                        Text(
                            text = "No medicines found in inventory.",
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(medicine) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it },
                    label = { Text("Manufacturer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Initial Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val stock = stockText.toIntOrNull() ?: 0
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    onAdd(name.trim(), manufacturer.trim(), stock, price)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
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
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("New Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("New Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newStock = stockText.toIntOrNull() ?: 0
                    val newPrice = priceText.toDoubleOrNull() ?: 0.0
                    onUpdate(newStock, newPrice)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
