package com.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanaesencia.app.data.UserEntity
import com.sanaesencia.app.data.UserRepository
import com.sanaesencia.app.data.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val professionals: StateFlow<List<UserEntity>> = repository.getUsersByRole(UserRole.PROFESIONAL)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun registerUser(user: UserEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertUser(user)
            onSuccess()
        }
    }

    fun login(run: String, password: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(run, password)
            if (user != null) {
                _currentUser.value = user
            }
            onResult(user)
        }
    }

    fun updateUserProfile(name: String, email: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val current = _currentUser.value ?: return@launch
            val updated = current.copy(name = name, email = email, phone = phone)
            repository.updateUser(updated)
            _currentUser.value = updated
            onSuccess()
        }
    }

    fun registerProfessional(user: UserEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertUser(user.copy(role = UserRole.PROFESIONAL))
            onSuccess()
        }
    }

    fun deleteProfessional(run: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser(run)
            onSuccess()
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

    fun logout() {
        _currentUser.value = null
    }
}
