package com.example.aplikasikasir.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplikasikasir.MainActivity
import com.example.aplikasikasir.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val name = sharedPref.getString("user_name", "User")
        val role = sharedPref.getString("user_role", "Kasir")

        binding.tvWelcome.text = "Selamat Datang, $name!"
        binding.tvRole.text = "Anda login sebagai ${role?.replaceFirstChar { it.uppercase() }}"

        binding.btnLogout.setOnClickListener {
            (activity as? MainActivity)?.performLogout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}