package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data sesi
        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("user_name", "User")
        val role = sharedPref.getString("user_role", "Kasir")

        // Tampilkan Welcome Message
        binding.tvWelcome.text = "Selamat Datang, $name!"
        binding.tvRole.text = "Anda login sebagai ${role?.replaceFirstChar { it.uppercase() }}"

        // Fungsi Logout
        binding.btnLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}