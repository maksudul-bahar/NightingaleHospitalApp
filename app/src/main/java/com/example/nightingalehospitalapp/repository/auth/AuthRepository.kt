package com.example.nightingalehospitalapp.repository.auth



import com.google.firebase.auth.FirebaseAuth
import com.example.nightingalehospitalapp.database.FirebaseConfig
import com.example.nightingalehospitalapp.models.user.User

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun registerUser(user: User, password: String, callback: (Boolean) -> Unit) {

        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener {

                if (it.isSuccessful) {

                    val uid = auth.currentUser!!.uid

                    FirebaseConfig.usersRef.child(uid).setValue(user)

                    callback(true)

                } else {
                    callback(false)
                }

            }
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }

    }
}