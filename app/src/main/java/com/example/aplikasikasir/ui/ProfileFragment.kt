package com.example.aplikasikasir.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplikasikasir.MainActivity
import com.example.aplikasikasir.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("user_name", "User")
        val role = sharedPref.getString("user_role", "Kasir")
        val email = sharedPref.getString("user_email", "-") // Kita perlu simpan email saat login nanti

        binding.tvProfileName.text = name
        binding.tvProfileRole.text = role?.replaceFirstChar { it.uppercase() }
        binding.tvProfileEmail.text = if (email == "-") "admin@pos.com" else email // Fallback dummy

        binding.btnLogout.setOnClickListener {
            (activity as? MainActivity)?.performLogout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}