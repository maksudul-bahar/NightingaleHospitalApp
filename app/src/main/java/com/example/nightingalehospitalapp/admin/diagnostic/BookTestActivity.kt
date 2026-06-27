package com.example.nightingalehospitalapp.admin.diagnostic

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
import com.example.nightingalehospitalapp.viewmodel.admin.diagnostic.BookTestViewModel

class BookTestActivity : ComponentActivity() {

    private val viewModel: BookTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            NightingaleHospitalAppTheme {
                BookTestScreen(
                    viewModel = viewModel,
                    onNavigateBack = { finish() },
                    onSuccess = {
                        Toast.makeText(this, "Diagnostic Test booked successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookTestScreen(
    viewModel: BookTestViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val patients by viewModel.patients.collectAsState()
    val doctors by viewModel.doctors.collectAsState()
    val diagnosticTests by viewModel.diagnosticTests.collectAsState()
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

    var selectedTestId by remember { mutableStateOf("") }
    var testSearchQuery by remember { mutableStateOf("") }
    var testExpanded by remember { mutableStateOf(false) }
    val filteredTests = diagnosticTests.filter { it.testName.contains(testSearchQuery, ignoreCase = true) || it.testId.contains(testSearchQuery, ignoreCase = true) }

    var date by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Diagnostic Test") },
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

                // Test Dropdown
                ExposedDropdownMenuBox(
                    expanded = testExpanded,
                    onExpandedChange = { testExpanded = !testExpanded }
                ) {
                    OutlinedTextField(
                        value = testSearchQuery,
                        onValueChange = {
                            testSearchQuery = it
                            testExpanded = true
                            selectedTestId = ""
                        },
                        readOnly = false,
                        label = { Text("Diagnostic Test") },
                        placeholder = { Text("Select or search test") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = testExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = testExpanded,
                        onDismissRequest = { testExpanded = false }
                    ) {
                        if (filteredTests.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No tests found") },
                                onClick = { testExpanded = false }
                            )
                        } else {
                            filteredTests.forEach { test ->
                                DropdownMenuItem(
                                    text = { Text("${test.testName} ($${test.price})") },
                                    onClick = {
                                        selectedTestId = test.testId
                                        testSearchQuery = test.testName
                                        testExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.submitTestBooking(
                            selectedPatientId,
                            selectedDoctorId,
                            selectedTestId,
                            date
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
                    Text("Book Test")
                }
            }
        }
    }
}
