package com.example.multimedia.ui.generalPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeneralPageViewModel : ViewModel() {

    // Przykładowe dane LiveData, możesz je modyfikować według swoich potrzeb
    private val _text = MutableLiveData<String>().apply {
        value = "To jest ekran GeneralPage"
    }
    val text: LiveData<String> = _text
}
