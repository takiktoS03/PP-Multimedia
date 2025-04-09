package com.example.multimedia.ui.generalPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.multimedia.databinding.FragmentGeneralPageBinding

class GeneralPageFragment : Fragment() {

    // Binding jest ważny między onCreateView a onDestroyView
    private var _binding: FragmentGeneralPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicjalizacja ViewModelu
        val generalPageViewModel = ViewModelProvider(this).get(GeneralPageViewModel::class.java)

        // Inicjalizacja bindingu
        _binding = FragmentGeneralPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Założenie: w layoutcie fragment_general_page.xml znajduje się TextView o id textGeneralPage
        val textView: TextView = binding.textGeneralPage
        generalPageViewModel.text.observe(viewLifecycleOwner) { newText ->
            textView.text = newText
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
