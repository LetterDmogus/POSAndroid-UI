package com.example.aplikasikasir

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val invoiceNo = intent.getStringExtra("EXTRA_INVOICE") ?: ""
        binding.tvSuccessInvoice.text = "No. Invoice: $invoiceNo"

        binding.btnViewReceipt.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java)
            intent.putExtra("EXTRA_INVOICE", invoiceNo)
            startActivity(intent)
        }

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}