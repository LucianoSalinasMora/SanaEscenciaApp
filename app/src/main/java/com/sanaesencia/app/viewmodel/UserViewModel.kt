package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.UserEntity
import com.sanaesencia.app.data.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun registerUser(user: UserEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertUser(user)
            onSuccess()
        }
    }

    fun login(run: String, password: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(run, password)
            onResult(user)
        }
    }

    fun getUserByRun(run: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByRun(run)
            onResult(user)
        }
    }

    fun updatePassword(run: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updatePassword(run, newPassword)
            onSuccess()
        }
    }
}

