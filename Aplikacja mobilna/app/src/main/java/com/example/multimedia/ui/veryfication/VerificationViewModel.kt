package com.example.multimedia.ui.veryfication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VerificationViewModel : ViewModel() {
    private val _trigger = MutableStateFlow(0)
    val trigger: StateFlow<Int> = _trigger

    fun reloadVerificationStatus() {
        _trigger.value += 1
    }
}