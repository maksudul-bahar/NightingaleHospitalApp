package com.example.nightingalehospitalapp.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseConfig {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val rootRef: DatabaseReference =
        database.getReference("nightingale_hospital")

    val usersRef = rootRef.child("users")

    val doctorsRef = rootRef.child("doctors")

    val patientsRef = rootRef.child("patients")

    val departmentsRef = rootRef.child("departments")

    val bedsRef = rootRef.child("beds")

    val operationTheatresRef = rootRef.child("operation_theatres")

    val appointmentsRef = rootRef.child("appointments")

    val prescriptionsRef = rootRef.child("prescriptions")

    val medicinesRef = rootRef.child("medicines")

    val testBookingsRef = rootRef.child("test_bookings")

    val testResultsRef = rootRef.child("test_results")

    val surgeryBookingsRef = rootRef.child("surgery_bookings")

    val notificationsRef = rootRef.child("notifications")

    val admissionsRef = rootRef.child("admissions")

}