package com.example.nightingalehospitalapp.patient

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
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nightingalehospitalapp.models.prescription.Medicine
import com.example.nightingalehospitalapp.ui.components.NightingaleElevatedCard
import com.example.nightingalehospitalapp.ui.components.NightingaleEmptyState
import com.example.nightingalehospitalapp.ui.components.NightingaleListShimmer
import com.example.nightingalehospitalapp.ui.components.NightingaleTextField
import com.example.nightingalehospitalapp.ui.components.NightingaleUserScaffold
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.MedicineViewModel

class ViewMedicineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = MedicineViewModel()

        setContent {
            NightingaleHospitalAppTheme {
                ViewMedicineScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewMedicineScreen(
    viewModel: MedicineViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    NightingaleUserScaffold(
        title = "Hospital Stock",
        showBottomBar = false,
        onNavigateBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            NightingaleTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = "Search medicine...",
                leadingIcon = Icons.Filled.Search
            )

            when (val state = uiState) {
                is MedicineViewModel.UiState.Loading -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
                        items(4) { NightingaleListShimmer() }
                    }
                }
                is MedicineViewModel.UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is MedicineViewModel.UiState.Loaded -> {
                    val filteredList = state.medicines.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.manufacturer.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredList.isEmpty()) {
                        NightingaleEmptyState(
                            title = "No medicines found",
                            message = "Try a different search query.",
                            icon = Icons.Filled.Inventory
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList) { medicine ->
                                MedicineCard(medicine)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MedicineCard(medicine: Medicine) {
    NightingaleElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Inventory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = medicine.manufacturer,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${medicine.stock}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (medicine.stock > 20) MaterialTheme.colorScheme.primary else Color.Red
                )
                Text(
                    text = "In Stock",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
