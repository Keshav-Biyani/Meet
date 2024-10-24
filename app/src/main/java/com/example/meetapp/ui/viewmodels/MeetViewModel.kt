package com.example.meetapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.meetapp.data.repository.MeetRepository
import com.google.apps.meet.v2.Space
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MeetViewModel(private val meetRepository: MeetRepository) : ViewModel() {

    private val _meetSpace = MutableStateFlow<Space?>(null)
    val meetSpace: StateFlow<Space?> = _meetSpace

    fun createMeetSpace() {
        viewModelScope.launch {
            meetRepository.createGoogleMeetSpace(
                onSuccess = { space -> _meetSpace.value = space

                        },
                onError = { e -> e.printStackTrace() }
            )
        }
    }
    @Suppress("UNCHECKED_CAST")
    class MeetViewModelFactory(private val repository: MeetRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MeetViewModel::class.java)) {
                return MeetViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}