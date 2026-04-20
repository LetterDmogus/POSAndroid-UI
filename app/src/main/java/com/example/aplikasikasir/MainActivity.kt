package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aplikasikasir.databinding.ActivityMainBinding
import com.example.aplikasikasir.ui.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val role = sharedPref.getString("user_role", "kasir")

        // Sembunyikan menu Master jika bukan admin
        val menuMaster = binding.bottomNavigation.menu.findItem(R.id.nav_master)
        menuMaster.isVisible = (role == "admin")

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
                R.id.nav_master -> {
                    // SEKARANG MEMANGGIL FRAGMENT (Navbar tidak akan hilang)
                    loadFragment(MasterDataFragment())
                    true
                }
                R.id.nav_orders -> {
                    loadFragment(OrdersFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
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

    fun performLogout() {
        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}