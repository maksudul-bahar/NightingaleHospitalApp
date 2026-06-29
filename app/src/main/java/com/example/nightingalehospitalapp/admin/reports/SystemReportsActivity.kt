package com.example.nightingalehospitalapp.admin.reports

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nightingalehospitalapp.ui.components.NightingaleElevatedCard
import com.example.nightingalehospitalapp.ui.components.NightingaleEmptyState
import com.example.nightingalehospitalapp.ui.components.NightingaleListShimmer
import com.example.nightingalehospitalapp.ui.components.NightingalePrimaryButton
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme
import com.example.nightingalehospitalapp.viewmodel.admin.reports.SystemReportsViewModel

class SystemReportsActivity : ComponentActivity() {
    private val viewModel: SystemReportsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NightingaleHospitalAppTheme {
                val activities by viewModel.activities.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("System Reports") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Activity Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        if (isLoading) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(3) { NightingaleListShimmer() }
                            }
                        } else if (activities.isEmpty()) {
                            NightingaleEmptyState(
                                title = "No Recent Activity",
                                message = "There is no recent activity to display.",
                                icon = Icons.Filled.Info
                            )
                        } else {
                            NightingaleElevatedCard {
                                Text("Recent System Activities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                activities.forEach { activity ->
                                    Text("• $activity")
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        val context = androidx.compose.ui.platform.LocalContext.current
                        NightingalePrimaryButton(
                            text = "Generate Full Report",
                            onClick = {
                                viewModel.generateFullReport()
                                android.widget.Toast.makeText(context, "Report generated successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
