package com.example.multimedia.ui.otherMedia

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimedia.data.model.MediaType
import com.example.multimedia.data.model.OtherMedia
import com.example.multimedia.data.repository.OtherMediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherMediaViewModel @Inject constructor(
    private val repository: OtherMediaRepository
) : ViewModel() {

    private val _mediaList = MutableStateFlow<List<OtherMedia>>(emptyList())
    val mediaList: StateFlow<List<OtherMedia>> = _mediaList

    init {
        viewModelScope.launch {
            _mediaList.value = repository.getUserMedia()
        }
    }

    fun uploadMedia(uri: Uri, title: String, desc: String, type: MediaType) {
        viewModelScope.launch {
            repository.addMedia(uri, title, desc, type)
            _mediaList.value = repository.getUserMedia()
        }
    }
}
