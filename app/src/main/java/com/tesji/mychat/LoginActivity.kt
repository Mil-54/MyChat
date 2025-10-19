package com.tesji.mychat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.tesji.mychat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Si el usuario ya ha iniciado sesión, llévalo directamente a MainActivity
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.buttonLogin.setOnClickListener {
            // MEJORA: Obtenemos el texto de los nuevos TextInputEditText
            val email = binding.textInputEmail.text.toString()
            val password = binding.textInputPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // MEJORA: Manejo de errores específicos para el inicio de sesión
                        val exception = task.exception
                        val message = when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> "El correo o la contraseña son incorrectos."
                            else -> "Fallo en la autenticación: ${exception?.message}"
                        }
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, rellena todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            // MEJORA: Obtenemos el texto de los nuevos TextInputEditText
            val email = binding.textInputEmail.text.toString()
            val password = binding.textInputPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registro exitoso. Por favor, inicia sesión.", Toast.LENGTH_SHORT).show()
                    } else {
                        // MEJORA: Manejo de errores específicos para el registro
                        val exception = task.exception
                        val message = when (exception) {
                            is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil. Debe tener al menos 6 caracteres."
                            is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado."
                            is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico no es válido."
                            else -> "Fallo en el registro: ${exception?.message}"
                        }
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, rellena todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

