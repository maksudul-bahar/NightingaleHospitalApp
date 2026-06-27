package com.example.nightingalehospitalapp.admin.surgery

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.admin.surgery.ScheduleSurgeryViewModel

class ScheduleSurgeryActivity : ComponentActivity() {
    private val viewModel: ScheduleSurgeryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NightingaleHospitalAppTheme {
                ScheduleSurgeryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { finish() },
                    onSuccess = {
                        Toast.makeText(this, "Surgery scheduled successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh patients/doctors/OTs so newly-added entries (e.g. an admin
        // creating a doctor or OT while this screen sat in the back stack)
        // are reflected when we return.
        viewModel.refresh()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSurgeryScreen(
    viewModel: ScheduleSurgeryViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val patients by viewModel.patients.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val operationTheatres by viewModel.operationTheatres.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var selectedPatientId by remember { mutableStateOf("") }
    var patientSearchQuery by remember { mutableStateOf("") }
    var patientExpanded by remember { mutableStateOf(false) }
    val filteredPatients = patients.filter { it.name.contains(patientSearchQuery, ignoreCase = true) || it.userId.contains(patientSearchQuery, ignoreCase = true) || it.displayId.contains(patientSearchQuery, ignoreCase = true) }

    var selectedDoctorId by remember { mutableStateOf("") }
    var doctorSearchQuery by remember { mutableStateOf("") }
    var doctorExpanded by remember { mutableStateOf(false) }
    val filteredDoctors = doctors.filter { it.name.contains(doctorSearchQuery, ignoreCase = true) || it.doctorId.contains(doctorSearchQuery, ignoreCase = true) || it.displayId.contains(doctorSearchQuery, ignoreCase = true) }

    var selectedOtId by remember { mutableStateOf("") }
    var otSearchQuery by remember { mutableStateOf("") }
    var otExpanded by remember { mutableStateOf(false) }
    val filteredOts = operationTheatres.filter { it.roomNumber.contains(otSearchQuery, ignoreCase = true) || it.otId.contains(otSearchQuery, ignoreCase = true) }

    var surgeryType by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Surgery") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Dropdown
                ExposedDropdownMenuBox(
                    expanded = patientExpanded,
                    onExpandedChange = { patientExpanded = !patientExpanded }
                ) {
                    OutlinedTextField(
                        value = patientSearchQuery,
                        onValueChange = {
                            patientSearchQuery = it
                            patientExpanded = true
                            selectedPatientId = ""
                        },
                        readOnly = false,
                        label = { Text("Patient") },
                        placeholder = { Text("Select or search patient") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = patientExpanded,
                        onDismissRequest = { patientExpanded = false }
                    ) {
                        filteredPatients.forEach { patient ->
                            DropdownMenuItem(
                                text = { Text("${patient.name} (${patient.displayId.ifEmpty { patient.userId }})") },
                                onClick = {
                                    selectedPatientId = patient.userId
                                    patientSearchQuery = "${patient.name} (${patient.displayId.ifEmpty { patient.userId }})"
                                    patientExpanded = false
                                }
                            )
                        }
                    }
                }

                // Doctor Dropdown
                ExposedDropdownMenuBox(
                    expanded = doctorExpanded,
                    onExpandedChange = { doctorExpanded = !doctorExpanded }
                ) {
                    OutlinedTextField(
                        value = doctorSearchQuery,
                        onValueChange = {
                            doctorSearchQuery = it
                            doctorExpanded = true
                            selectedDoctorId = ""
                        },
                        readOnly = false,
                        label = { Text("Doctor") },
                        placeholder = { Text("Select or search doctor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = doctorExpanded,
                        onDismissRequest = { doctorExpanded = false }
                    ) {
                        filteredDoctors.forEach { doctor ->
                            DropdownMenuItem(
                                text = { Text("${doctor.name} (${doctor.displayId.ifEmpty { doctor.doctorId }})") },
                                onClick = {
                                    selectedDoctorId = doctor.doctorId
                                    doctorSearchQuery = "${doctor.name} (${doctor.displayId.ifEmpty { doctor.doctorId }})"
                                    doctorExpanded = false
                                }
                            )
                        }
                    }
                }

                // OT Dropdown
                ExposedDropdownMenuBox(
                    expanded = otExpanded,
                    onExpandedChange = { otExpanded = !otExpanded }
                ) {
                    OutlinedTextField(
                        value = otSearchQuery,
                        onValueChange = {
                            otSearchQuery = it
                            otExpanded = true
                            selectedOtId = ""
                        },
                        readOnly = false,
                        label = { Text("Operation Theatre") },
                        placeholder = { Text("Select or search OT") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = otExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = otExpanded,
                        onDismissRequest = { otExpanded = false }
                    ) {
                        if (filteredOts.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No OTs found") },
                                onClick = { otExpanded = false }
                            )
                        } else {
                            filteredOts.forEach { ot ->
                                DropdownMenuItem(
                                    text = { Text("Room ${ot.roomNumber} (Floor ${ot.floor})") },
                                    onClick = {
                                        selectedOtId = ot.otId
                                        otSearchQuery = "Room ${ot.roomNumber} (Floor ${ot.floor})"
                                        otExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = surgeryType,
                    onValueChange = { surgeryType = it },
                    label = { Text("Surgery Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.submitSurgery(
                            selectedPatientId,
                            selectedDoctorId,
                            selectedOtId,
                            surgeryType,
                            date,
                            startTime,
                            endTime
                        ) { success, msg ->
                            if (success) {
                                onSuccess()
                            } else {
                                Toast.makeText(context, msg ?: "Error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Schedule Surgery")
                }
            }
        }
    }
}
