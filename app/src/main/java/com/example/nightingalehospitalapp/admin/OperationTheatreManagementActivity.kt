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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightingalehospitalapp.models.hospital.OperationTheatre
import com.example.nightingalehospitalapp.models.user.User
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.OperationTheatreViewModel

class OperationTheatreManagementActivity : ComponentActivity() {

    private val viewModel: OperationTheatreViewModel by viewModels()

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
                            title = { Text("Operation Theatre Management") },
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
                            Icon(Icons.Filled.Add, contentDescription = "Add OT")
                        }
                    }
                ) { paddingValues ->
                    OTManagementContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        viewModel = viewModel
                    )

                    if (showAddDialog) {
                        AddEditOTDialog(
                            theatre = null,
                            doctors = viewModel.doctors.collectAsState().value,
                            onDismiss = { showAddDialog = false },
                            onConfirm = { room, floor, status, capacity, sched, docId, docName ->
                                viewModel.addTheatre(room, floor, status, capacity, sched, docId, docName)
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
private fun OTManagementContent(
    modifier: Modifier = Modifier,
    viewModel: OperationTheatreViewModel
) {
    val state by viewModel.theatresState.collectAsState()
    val doctors by viewModel.doctors.collectAsState()

    var editingTheatre by remember { mutableStateOf<OperationTheatre?>(null) }
    var updatingStatusTheatre by remember { mutableStateOf<OperationTheatre?>(null) }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        when (val s = state) {
            is OperationTheatreViewModel.UiState.Idle,
            is OperationTheatreViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is OperationTheatreViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${s.message}", color = MaterialTheme.colorScheme.error)
            }

            is OperationTheatreViewModel.UiState.Loaded -> {
                if (s.theatres.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No operation theatres found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.theatres, key = { it.otId }) { theatre ->
                            OTCard(
                                theatre = theatre,
                                onEditClick = { editingTheatre = theatre },
                                onStatusClick = { updatingStatusTheatre = theatre },
                                onDeleteClick = { viewModel.deleteTheatre(theatre.otId) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (editingTheatre != null) {
        AddEditOTDialog(
            theatre = editingTheatre,
            doctors = doctors,
            onDismiss = { editingTheatre = null },
            onConfirm = { room, floor, status, capacity, sched, docId, docName ->
                editingTheatre?.let {
                    val updated = it.copy(
                        roomNumber = room,
                        floor = floor,
                        status = status,
                        capacity = capacity,
                        schedule = sched,
                        assignedDoctorId = docId,
                        assignedDoctorName = docName
                    )
                    viewModel.updateTheatre(updated)
                }
                editingTheatre = null
            }
        )
    }

    if (updatingStatusTheatre != null) {
        UpdateOTStatusDialog(
            theatre = updatingStatusTheatre!!,
            onDismiss = { updatingStatusTheatre = null },
            onStatusSelected = { newStatus ->
                updatingStatusTheatre?.let {
                    // Automatically handle doctor/schedule clear if status is CLEANING or FREE
                    val updated = if (newStatus == "FREE" || newStatus == "CLEANING") {
                        it.copy(status = newStatus, assignedDoctorId = null, assignedDoctorName = null, schedule = null)
                    } else {
                        it.copy(status = newStatus)
                    }
                    viewModel.updateTheatre(updated)
                }
                updatingStatusTheatre = null
            }
        )
    }
}

@Composable
private fun OTCard(
    theatre: OperationTheatre,
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
                        text = "OT ID/Name: ${theatre.roomNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${theatre.floor} • Max Capacity: ${theatre.capacity}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OTStatusChip(status = theatre.status, onClick = onStatusClick)
            }

            if (!theatre.assignedDoctorName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Doctor: ${theatre.assignedDoctorName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!theatre.schedule.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Schedule: ${theatre.schedule}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun OTStatusChip(status: String, onClick: () -> Unit) {
    val (bg, fg) = when (status) {
        "FREE" -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
        "IN_USE" -> Color(0xFFBBDEFB) to Color(0xFF0D47A1)
        "CLEANING" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        else -> Color(0xFFE0E0E0) to Color(0xFF424242)
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = status,
            color = fg,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditOTDialog(
    theatre: OperationTheatre?,
    doctors: List<User>,
    onDismiss: () -> Unit,
    onConfirm: (room: String, floor: String, status: String, capacity: Int, schedule: String?, docId: String?, docName: String?) -> Unit
) {
    var roomNumber by remember { mutableStateOf(theatre?.roomNumber ?: "") }
    var floor by remember { mutableStateOf(theatre?.floor ?: "") }
    var status by remember { mutableStateOf(theatre?.status ?: "FREE") }
    var capacityStr by remember { mutableStateOf(theatre?.capacity?.toString() ?: "1") }
    var schedule by remember { mutableStateOf(theatre?.schedule ?: "") }
    var docId by remember { mutableStateOf(theatre?.assignedDoctorId) }
    var docName by remember { mutableStateOf(theatre?.assignedDoctorName) }

    var roomError by remember { mutableStateOf(false) }
    var floorError by remember { mutableStateOf(false) }

    var doctorExpanded by remember { mutableStateOf(false) }
    var selectedDoctorText by remember {
        mutableStateOf(
            if (theatre?.assignedDoctorId != null) {
                "${theatre.assignedDoctorName ?: ""} (${theatre.assignedDoctorId})"
            } else {
                "None"
            }
        )
    }

    var statusExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("FREE", "IN_USE", "CLEANING")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (theatre == null) "Add Operation Theatre" else "Edit Operation Theatre") },
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
                    label = { Text("OT Name / Number") },
                    isError = roomError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = floor,
                    onValueChange = {
                        floor = it
                        floorError = it.isBlank()
                    },
                    label = { Text("Floor") },
                    isError = floorError,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = capacityStr,
                    onValueChange = { capacityStr = it },
                    label = { Text("Max Doctor/Patient Capacity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Status dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = status,
                        onValueChange = {},
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    status = s
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                // If status is IN_USE, open up fields for Doctor & Schedule
                if (status == "IN_USE") {
                    ExposedDropdownMenuBox(
                        expanded = doctorExpanded,
                        onExpandedChange = { doctorExpanded = !doctorExpanded }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedDoctorText,
                            onValueChange = {},
                            label = { Text("Assigned Doctor") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = doctorExpanded,
                            onDismissRequest = { doctorExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    docId = null
                                    docName = null
                                    selectedDoctorText = "None"
                                    doctorExpanded = false
                                }
                            )
                            doctors.forEach { doc ->
                                DropdownMenuItem(
                                    text = { Text("${doc.name} (${doc.userId})") },
                                    onClick = {
                                        docId = doc.userId
                                        docName = doc.name
                                        selectedDoctorText = "${doc.name} (${doc.userId})"
                                        doctorExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = schedule,
                        onValueChange = { schedule = it },
                        label = { Text("Schedule (e.g., 10:00 - 12:00)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    roomError = roomNumber.isBlank()
                    floorError = floor.isBlank()
                    if (!roomError && !floorError) {
                        val capacity = capacityStr.toIntOrNull() ?: 1
                        val cleanSched = if (status == "IN_USE") schedule else null
                        val cleanDocId = if (status == "IN_USE") docId else null
                        val cleanDocName = if (status == "IN_USE") docName else null
                        onConfirm(roomNumber, floor, status, capacity, cleanSched, cleanDocId, cleanDocName)
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
private fun UpdateOTStatusDialog(
    theatre: OperationTheatre,
    onDismiss: () -> Unit,
    onStatusSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status: ${theatre.roomNumber}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statuses = listOf("FREE", "IN_USE", "CLEANING")
                statuses.forEach { s ->
                    val (bg, fg) = when (s) {
                        "FREE" -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
                        "IN_USE" -> Color(0xFFBBDEFB) to Color(0xFF0D47A1)
                        "CLEANING" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
                        else -> Color(0xFFE0E0E0) to Color(0xFF424242)
                    }
                    Button(
                        onClick = { onStatusSelected(s) },
                        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(s, fontWeight = FontWeight.Bold)
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
