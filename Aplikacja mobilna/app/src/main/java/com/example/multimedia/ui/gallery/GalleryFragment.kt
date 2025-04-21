package com.example.multimedia.ui.gallery

import PhotoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.multimedia.databinding.FragmentGalleryBinding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PhotoAdapter()
        binding.recyclerViewGallery.adapter = adapter
        binding.recyclerViewGallery.layoutManager = LinearLayoutManager(requireContext())

        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            adapter.submitList(photos)
        }

        viewModel.text.observe(viewLifecycleOwner) {
            binding.textGallery.text = it
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}