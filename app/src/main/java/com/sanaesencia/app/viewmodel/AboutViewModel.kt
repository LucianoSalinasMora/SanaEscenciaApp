package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.AboutInfoEntity
import com.sanaesencia.app.data.AboutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AboutViewModel(private val repository: AboutRepository) : ViewModel() {
    val aboutInfo: StateFlow<AboutInfoEntity?> = repository.aboutInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AboutInfoEntity()
        )

    init {
        viewModelScope.launch {
            // Seed initial about info if not present
            repository.saveAboutInfo(AboutInfoEntity())
        }
    }

    fun updateAboutInfo(info: AboutInfoEntity) {
        viewModelScope.launch {
            repository.saveAboutInfo(info)
        }
    }
}
