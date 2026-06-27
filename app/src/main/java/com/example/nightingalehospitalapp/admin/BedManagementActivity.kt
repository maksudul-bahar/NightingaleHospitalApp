package com.example.nightingalehospitalapp.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightingalehospitalapp.models.hospital.Bed
import com.example.nightingalehospitalapp.models.enums.BedStatus
import com.example.nightingalehospitalapp.models.user.User
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.BedViewModel

class BedManagementActivity : ComponentActivity() {

    private val viewModel: BedViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NightingaleHospitalAppTheme {
                val context = LocalContext.current
                val operationResult by viewModel.operationResult.collectAsState()

                LaunchedEffect(operationResult) {
                    operationResult?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearOperationResult()
                    }
                }

                var showAddDialog by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Bed Management") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Bed")
                        }
                    }
                ) { padding ->
                    BedManagementContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        viewModel = viewModel
                    )

                    if (showAddDialog) {
                        AddEditBedDialog(
                            bed = null,
                            patients = viewModel.patients.collectAsState().value,
                            onDismiss = { showAddDialog = false },
                            onConfirm = { room, ward, type, status, patId, patName ->
                                viewModel.addBed(room, ward, type, status)
                                showAddDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BedManagementContent(
    modifier: Modifier = Modifier,
    viewModel: BedViewModel
) {
    val state by viewModel.bedsState.collectAsState()
    val patients by viewModel.patients.collectAsState()

    var editingBed by remember { mutableStateOf<Bed?>(null) }
    var updatingStatusBed by remember { mutableStateOf<Bed?>(null) }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        when (val s = state) {
            is BedViewModel.UiState.Idle,
            is BedViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is BedViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${s.message}", color = MaterialTheme.colorScheme.error)
            }

            is BedViewModel.UiState.Loaded -> {
                if (s.beds.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No beds found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.beds, key = { it.bedId }) { bed ->
                            BedCard(
                                bed = bed,
                                onEditClick = { editingBed = bed },
                                onStatusClick = { updatingStatusBed = bed },
                                onDeleteClick = { viewModel.deleteBed(bed.bedId) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (editingBed != null) {
        AddEditBedDialog(
            bed = editingBed,
            patients = patients,
            onDismiss = { editingBed = null },
            onConfirm = { room, ward, type, status, patId, patName ->
                editingBed?.let {
                    val updated = it.copy(
                        roomNumber = room,
                        ward = ward,
                        bedType = type,
                        status = status,
                        patientId = patId,
                        patientName = patName
                    )
                    viewModel.updateBed(updated)
                }
                editingBed = null
            }
        )
    }

    if (updatingStatusBed != null) {
        UpdateStatusDialog(
            bed = updatingStatusBed!!,
            onDismiss = { updatingStatusBed = null },
            onStatusSelected = { newStatus ->
                updatingStatusBed?.let {
                    // Automatically handle patient clear if status is not OCCUPIED
                    val updated = if (newStatus != BedStatus.OCCUPIED) {
                        it.copy(status = newStatus, patientId = null, patientName = null)
                    } else {
                        it.copy(status = newStatus)
                    }
                    viewModel.updateBed(updated)
                }
                updatingStatusBed = null
            }
        )
    }
}

@Composable
private fun BedCard(
    bed: Bed,
    onEditClick: () -> Unit,
    onStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Bed ID/No: ${bed.roomNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${bed.ward} • ${bed.bedType}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BedStatusChip(status = bed.status, onClick = onStatusClick)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (bed.status == BedStatus.OCCUPIED && !bed.patientName.isNullOrBlank()) {
                Text(
                    text = "Assigned Patient: ${bed.patientName} (${bed.patientId ?: "N/A"})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else if (bed.status == BedStatus.OCCUPIED) {
                Text(
                    text = "Assigned Patient: ${bed.patientId ?: "Unknown Patient"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun BedStatusChip(status: BedStatus, onClick: () -> Unit) {
    val (bg, fg) = when (status) {
        BedStatus.AVAILABLE -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
        BedStatus.OCCUPIED -> Color(0xFFBBDEFB) to Color(0xFF0D47A1)
        BedStatus.MAINTENANCE -> Color(0xFFFFE0B2) to Color(0xFFE65100)
        BedStatus.CLEANING -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = status.name,
            color = fg,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditBedDialog(
    bed: Bed?,
    patients: List<User>,
    onDismiss: () -> Unit,
    onConfirm: (room: String, ward: String, type: String, status: BedStatus, patId: String?, patName: String?) -> Unit
) {
    var roomNumber by remember { mutableStateOf(bed?.roomNumber ?: "") }
    var ward by remember { mutableStateOf(bed?.ward ?: "") }
    var bedType by remember { mutableStateOf(bed?.bedType ?: "General") }
    var status by remember { mutableStateOf(bed?.status ?: BedStatus.AVAILABLE) }
    var patientId by remember { mutableStateOf(bed?.patientId) }
    var patientName by remember { mutableStateOf(bed?.patientName) }

    var roomError by remember { mutableStateOf(false) }
    var wardError by remember { mutableStateOf(false) }

    // Patient Dropdown State
    var patientExpanded by remember { mutableStateOf(false) }
    var selectedPatientText by remember {
        mutableStateOf(
            if (bed?.patientId != null) {
                "${bed.patientName ?: ""} (${bed.patientId})"
            } else {
                "None"
            }
        )
    }

    // Bed Type Dropdown State
    var typeExpanded by remember { mutableStateOf(false) }
    val bedTypes = listOf("General", "Semi-Private", "ICU")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (bed == null) "Add Bed" else "Edit Bed") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = {
                        roomNumber = it
                        roomError = it.isBlank()
                    },
                    label = { Text("Bed/Room Number") },
                    isError = roomError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ward,
                    onValueChange = {
                        ward = it
                        wardError = it.isBlank()
                    },
                    label = { Text("Ward") },
                    isError = wardError,
                    modifier = Modifier.fillMaxWidth()
                )

                // Bed Type Selector
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = bedType,
                        onValueChange = {},
                        label = { Text("Bed Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        bedTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    bedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // If bed status is OCCUPIED, let Admin select patient
                if (status == BedStatus.OCCUPIED) {
                    ExposedDropdownMenuBox(
                        expanded = patientExpanded,
                        onExpandedChange = { patientExpanded = !patientExpanded }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedPatientText,
                            onValueChange = {},
                            label = { Text("Assigned Patient") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = patientExpanded,
                            onDismissRequest = { patientExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    patientId = null
                                    patientName = null
                                    selectedPatientText = "None"
                                    patientExpanded = false
                                }
                            )
                            patients.forEach { patient ->
                                DropdownMenuItem(
                                    text = { Text("${patient.name} (${patient.userId})") },
                                    onClick = {
                                        patientId = patient.userId
                                        patientName = patient.name
                                        selectedPatientText = "${patient.name} (${patient.userId})"
                                        patientExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    roomError = roomNumber.isBlank()
                    wardError = ward.isBlank()
                    if (!roomError && !wardError) {
                        onConfirm(roomNumber, ward, bedType, status, patientId, patientName)
                    }
                }
            ) {
                Text("Save")
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
private fun UpdateStatusDialog(
    bed: Bed,
    onDismiss: () -> Unit,
    onStatusSelected: (BedStatus) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Bed Status: ${bed.roomNumber}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BedStatus.values().forEach { status ->
                    val (bg, fg) = when (status) {
                        BedStatus.AVAILABLE -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
                        BedStatus.OCCUPIED -> Color(0xFFBBDEFB) to Color(0xFF0D47A1)
                        BedStatus.MAINTENANCE -> Color(0xFFFFE0B2) to Color(0xFFE65100)
                        BedStatus.CLEANING -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
                    }
                    Button(
                        onClick = { onStatusSelected(status) },
                        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(status.name, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
