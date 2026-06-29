package com.example.nightingalehospitalapp.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A highly scalable layout component for Admin activities.
 * It automatically wraps the content in a ModalNavigationDrawer and a Scaffold.
 * 
 * @param title The title to display in the TopAppBar.
 * @param context The current context, used for launching Intents from the drawer.
 * @param currentActivityClass The class of the current activity to avoid relaunching itself (optional).
 * @param content The main content of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NightingaleAdminScaffold(
    title: String,
    context: Context = LocalContext.current,
    currentActivityClass: Class<*>? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Admin Menu", 
                    modifier = Modifier.padding(16.dp), 
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider()
                
                // Helper function to build drawer items
                @Composable
                fun DrawerItem(label: String, icon: ImageVector, targetClass: Class<*>) {
                    val isSelected = currentActivityClass == targetClass
                    NavigationDrawerItem(
                        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
                        selected = isSelected,
                        icon = { Icon(icon, contentDescription = null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (!isSelected) {
                                val intent = Intent(context, targetClass)
                                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                DrawerItem("Dashboard", Icons.Filled.Home, Class.forName("com.example.nightingalehospitalapp.admin.AdminDashboardActivity"))
                DrawerItem("Manage Doctors", Icons.Filled.Face, Class.forName("com.example.nightingalehospitalapp.admin.doctors.ManageDoctorsActivity"))
                DrawerItem("Manage Resources", Icons.Filled.Build, Class.forName("com.example.nightingalehospitalapp.admin.resources.ManageResourcesActivity"))
                DrawerItem("Manage Surgeries", Icons.Filled.Settings, Class.forName("com.example.nightingalehospitalapp.admin.surgery.ManageSurgeriesActivity"))
                DrawerItem("Manage Admissions", Icons.Filled.Info, Class.forName("com.example.nightingalehospitalapp.admin.admissions.ManageAdmissionsActivity"))
                DrawerItem("Manage Tests", Icons.Filled.DateRange, Class.forName("com.example.nightingalehospitalapp.admin.diagnostic.ManageTestBookingsActivity"))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            context.startActivity(Intent(context, Class.forName("com.example.nightingalehospitalapp.activities.ProfileActivity")))
                        }) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

/**
 * A scalable layout component for Doctor and Patient activities.
 * It automatically includes a standard Bottom Navigation Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NightingaleUserScaffold(
    title: String,
    currentTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentTab == 0,
                        onClick = { onTabSelected(0) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.DateRange, contentDescription = "Appointments") },
                        label = { Text("Appointments") },
                        selected = currentTab == 1,
                        onClick = { onTabSelected(1) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = currentTab == 2,
                        onClick = { onTabSelected(2) }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = content
    )
}
