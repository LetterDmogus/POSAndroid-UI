package com.example.aplikasikasir

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.data.model.Category
import com.example.aplikasikasir.databinding.ActivityFormBarangBinding
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class FormBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBarangBinding
    private var token: String = ""
    private var isEdit = false
    private var barangId: Int = 0
    private var listCategories = mutableListOf<Category>()
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivFormFoto.setPadding(0, 0, 0, 0)
            Picasso.get().load(selectedImageUri).into(binding.ivFormFoto)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        fetchCategories()

        val barang = intent.getParcelableExtra<Barang>("EXTRA_BARANG")
        if (barang != null) {
            isEdit = true
            barangId = barang.id
            setupEditMode(barang)
        }

        binding.btnBackFormBarang.setOnClickListener { finish() }
        binding.btnSelectImage.setOnClickListener { openGallery() }
        binding.btnSaveBarang.setOnClickListener { saveBarang() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun setupEditMode(barang: Barang) {
        binding.tvFormTitle.text = "Edit Barang"
        binding.etFormSku.setText(barang.sku)
        binding.etFormSku.isEnabled = false
        binding.etFormNama.setText(barang.namaBarang)
        binding.etFormStok.setText(barang.stok.toString())
        binding.etFormSatuan.setText(barang.satuan)
        binding.etFormHargaBeli.setText(barang.hargaBeli.toInt().toString())
        binding.etFormHargaJual.setText(barang.hargaJual.toInt().toString())
        binding.etFormDeskripsi.setText(barang.deskripsi)

        if (!barang.fotoUrl.isNullOrEmpty()) {
            binding.ivFormFoto.setPadding(0, 0, 0, 0)
            Picasso.get().load(barang.fotoUrl).into(binding.ivFormFoto)
        }
    }

    private fun fetchCategories() {
        RetrofitClient.instance.getCategories(token).enqueue(object : Callback<ApiResponse<List<Category>>> {
            override fun onResponse(call: Call<ApiResponse<List<Category>>>, response: Response<ApiResponse<List<Category>>>) {
                if (response.isSuccessful) {
                    listCategories.clear()
                    listCategories.addAll(response.body()?.data ?: emptyList())
                    val names = listCategories.map { it.namaKategori }
                    val adapter = ArrayAdapter(this@FormBarangActivity, android.R.layout.simple_spinner_item, names)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spFormCategory.adapter = adapter
                    
                    val currentBarang = intent.getParcelableExtra<Barang>("EXTRA_BARANG")
                    currentBarang?.let { b ->
                        val index = listCategories.indexOfFirst { it.id == b.categoryId }
                        if (index != -1) binding.spFormCategory.setSelection(index)
                    }
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Category>>>, t: Throwable) {}
        })
    }

    private fun saveBarang() {
        // Menggunakan cara lama (Static create) agar kompatibel dengan v3 & v4
        val mediaType = MediaType.parse("text/plain")
        
        val sku = RequestBody.create(mediaType, binding.etFormSku.text.toString().trim())
        val nama = RequestBody.create(mediaType, binding.etFormNama.text.toString().trim())
        val stok = RequestBody.create(mediaType, binding.etFormStok.text.toString().trim())
        val satuan = RequestBody.create(mediaType, binding.etFormSatuan.text.toString().trim())
        val hargaBeli = RequestBody.create(mediaType, binding.etFormHargaBeli.text.toString().trim())
        val hargaJual = RequestBody.create(mediaType, binding.etFormHargaJual.text.toString().trim())
        val deskripsi = RequestBody.create(mediaType, binding.etFormDeskripsi.text.toString().trim())
        
        if (listCategories.isEmpty()) return
        val categoryId = RequestBody.create(mediaType, listCategories[binding.spFormCategory.selectedItemPosition].id.toString())

        var fotoPart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            val file = uriToFile(uri)
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            fotoPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
        }

        binding.btnSaveBarang.isEnabled = false
        binding.btnSaveBarang.text = "Menyimpan..."

        val call = if (isEdit) {
            RetrofitClient.instance.updateBarang(token, barangId, sku, nama, categoryId, stok, satuan, hargaBeli, hargaJual, deskripsi, fotoPart)
        } else {
            RetrofitClient.instance.createBarang(token, sku, nama, categoryId, stok, satuan, hargaBeli, hargaJual, deskripsi, fotoPart)
        }

        call.enqueue(object : Callback<ApiResponse<Barang>> {
            override fun onResponse(call: Call<ApiResponse<Barang>>, response: Response<ApiResponse<Barang>>) {
                binding.btnSaveBarang.isEnabled = true
                binding.btnSaveBarang.text = "SIMPAN BARANG"
                if (response.isSuccessful) {
                    Toast.makeText(this@FormBarangActivity, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Barang>>, t: Throwable) {
                binding.btnSaveBarang.isEnabled = true
                binding.btnSaveBarang.text = "SIMPAN BARANG"
            }
        })
    }

    private fun uriToFile(uri: Uri): File {
        val file = File(cacheDir, "temp_image.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}