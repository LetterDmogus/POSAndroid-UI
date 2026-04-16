package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.LoginResponse
import com.example.aplikasikasir.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val TAG = "DEBUG_LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        if (sharedPref.getString("token", null) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginData = mapOf("email" to email, "password" to password)
            Log.d(TAG, "Mencoba Login ke: ${email}")

            RetrofitClient.instance.login(loginData).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val code = response.code()
                    Log.d(TAG, "Response Code: $code")

                    when (code) {
                        200 -> {
                            val body = response.body()
                            if (body != null && body.success) {
                                val editor = sharedPref.edit()
                                editor.putString("token", body.token)
                                editor.putString("user_name", body.user?.name)
                                editor.putString("user_role", body.user?.role)
                                editor.apply()

                                Log.d(TAG, "Login Berhasil. Token: ${body.token}")
                                Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                Log.e(TAG, "200 OK tapi success=false: ${body?.message}")
                                Toast.makeText(this@LoginActivity, "Error: ${body?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        401 -> {
                            Log.e(TAG, "401 Unauthorized: Email atau Password salah.")
                            Toast.makeText(this@LoginActivity, "Login Gagal (401): Email/Password Salah", Toast.LENGTH_LONG).show()
                        }
                        404 -> {
                            Log.e(TAG, "404 Not Found: Endpoint API tidak ditemukan.")
                            Toast.makeText(this@LoginActivity, "Error (404): API tidak ditemukan di server", Toast.LENGTH_LONG).show()
                        }
                        422 -> {
                            Log.e(TAG, "422 Unprocessable Entity: Validasi gagal.")
                            Toast.makeText(this@LoginActivity, "Error (422): Input tidak valid", Toast.LENGTH_LONG).show()
                        }
                        500 -> {
                            Log.e(TAG, "500 Server Error: Ada masalah di Laravel kamu.")
                            Toast.makeText(this@LoginActivity, "Error (500): Server Laravel Error!", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Log.e(TAG, "HTTP Error Lainnya: $code")
                            Toast.makeText(this@LoginActivity, "Error HTTP: $code", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(TAG, "FATAL FAILURE: ${t.message}")
                    Toast.makeText(this@LoginActivity, "Koneksi Gagal Total: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}