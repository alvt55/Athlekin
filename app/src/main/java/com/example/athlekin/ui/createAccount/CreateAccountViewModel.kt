package com.example.athlekin.ui.createAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlekin.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shouldGoTracker = MutableStateFlow(false)
    val shouldGoTracker: StateFlow<Boolean>
        get() = _shouldGoTracker.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun createAccount(
        email: String,
        password: String,
        repeatPassword: String,
    ) {


        if (password != repeatPassword) {
            _errorMessage.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            val result = authRepository.createAccount(email, password)

            result
                .onSuccess {
                    _shouldGoTracker.value = true
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Sign up failed"
                }
        }


    }
}