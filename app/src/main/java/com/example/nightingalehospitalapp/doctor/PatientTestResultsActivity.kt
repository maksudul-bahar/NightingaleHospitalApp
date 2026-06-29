package com.example.nightingalehospitalapp.doctor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nightingalehospitalapp.ui.components.NightingaleElevatedCard
import com.example.nightingalehospitalapp.ui.components.NightingaleEmptyState
import com.example.nightingalehospitalapp.ui.components.NightingaleListShimmer
import com.example.nightingalehospitalapp.ui.components.NightingaleUserScaffold
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.TestResultsViewModel

class PatientTestResultsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val patientId = intent.getStringExtra(EXTRA_PATIENT_ID).orEmpty()
        val patientName = intent.getStringExtra(EXTRA_PATIENT_NAME) ?: "Patient"

        setContent {
            NightingaleHospitalAppTheme {
                PatientTestResultsScreen(
                    patientId = patientId,
                    patientName = patientName
                )
            }
        }
    }

    companion object {
        const val EXTRA_PATIENT_ID = "extra_patient_id"
        const val EXTRA_PATIENT_NAME = "extra_patient_name"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientTestResultsScreen(
    patientId: String,
    patientName: String,
    viewModel: TestResultsViewModel = viewModel()
) {
    val context = LocalContext.current

    // Start observing as soon as the screen enters composition.
    LaunchedEffect(patientId) {
        viewModel.observe(patientId)
    }

    val state by viewModel.uiState.collectAsState()

    NightingaleUserScaffold(
        title = "Test Results — $patientName",
        showBottomBar = false,
        onNavigateBack = {
            (context as? ComponentActivity)?.finish()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val s = state) {
                is TestResultsViewModel.UiState.Idle,
                is TestResultsViewModel.UiState.Loading -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) { NightingaleListShimmer() }
                    }
                }
                is TestResultsViewModel.UiState.Empty -> {
                    EmptyResultsState()
                }
                is TestResultsViewModel.UiState.Loaded -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.results, key = { it.resultId.ifEmpty { it.hashCode().toString() } }) { result ->
                            TestResultCard(result)
                        }
                    }
                }
                is TestResultsViewModel.UiState.Error -> {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun TestResultCard(result: com.example.nightingalehospitalapp.models.diagnostic.TestResult) {
    NightingaleElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Test Result",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = result.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.size(6.dp))
            if (result.results.isEmpty()) {
                Text(
                    text = "No results provided.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                result.results.forEach { item ->
                    Text(
                        text = "${item.problem}: ${item.result}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyResultsState() {
    NightingaleEmptyState(
        title = "No test results yet",
        message = "When the lab uploads results, they will appear here in real time.",
        icon = Icons.Filled.Info
    )
}