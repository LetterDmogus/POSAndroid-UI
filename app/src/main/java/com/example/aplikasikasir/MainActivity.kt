package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aplikasikasir.databinding.ActivityMainBinding
import com.example.aplikasikasir.ui.CatalogFragment
import com.example.aplikasikasir.ui.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set fragment default
        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_catalog -> {
                    loadFragment(CatalogFragment())
                    true
                }
                R.id.nav_profile -> {
                    // Sementara profile isinya tombol logout di Home, 
                    // tapi kita bisa tambahkan fragment khusus nanti.
                    loadFragment(HomeFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Fungsi Logout (Bisa dipanggil dari Fragment manapun lewat activity)
    fun performLogout() {
        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}