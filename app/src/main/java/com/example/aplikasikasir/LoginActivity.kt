package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek jika sudah login sebelumnya
        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        if (sharedPref.getString("token", null) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginData = mapOf("email" to email, "password" to password)

            RetrofitClient.instance.login(loginData).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success) {
                            // Simpan Sesi
                            val editor = sharedPref.edit()
                            editor.putString("token", body.token)
                            editor.putString("user_name", body.user?.name)
                            editor.putString("user_role", body.user?.role)
                            editor.apply()

                            Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Email atau Password Salah!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Koneksi Gagal: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}