package com.example.nightingalehospitalapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.nightingalehospitalapp.viewmodel.AuthViewModel
import com.example.nightingalehospitalapp.admin.AdminDashboardActivity
import com.example.nightingalehospitalapp.doctor.DoctorDashboardActivity
import com.example.nightingalehospitalapp.patient.PatientDashboardActivity
import com.example.nightingalehospitalapp.ui.theme.NightingaleHospitalAppTheme

class LoginActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NightingaleHospitalAppTheme {
                val context = LocalContext.current
                viewModel = ViewModelProvider(this@LoginActivity).get(AuthViewModel::class.java)
                LoginScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Enter email and password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.loginUser(email, password) { role, error ->
                        if (error != null) {
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        } else {
                            when (role) {
                                "ADMIN" -> {
                                    context.startActivity(Intent(context, AdminDashboardActivity::class.java))
                                }
                                "DOCTOR" -> {
                                    context.startActivity(Intent(context, DoctorDashboardActivity::class.java))
                                }
                                "PATIENT" -> {
                                    context.startActivity(Intent(context, PatientDashboardActivity::class.java))
                                }
                                else -> {
                                    Toast.makeText(context, "Invalid role", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
    }
}