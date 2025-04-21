package com.example.multimedia.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.multimedia.ui.pages.HomeScreen

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        val composeView = ComposeView(requireContext())

        composeView.setContent {
            val title = homeViewModel.text.collectAsState()
            val isLoading = homeViewModel.isLoading.collectAsState()
            HomeScreen(title.value, isLoading.value)
        }

        return composeView
    }

}