package com.example.athlekin.ui.login

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
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shouldGoTracker = MutableStateFlow(false)
    val shouldGoTracker: StateFlow<Boolean>
        get() = _shouldGoTracker.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun signIn(
        email: String,
        password: String,
//        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {


        viewModelScope.launch {
            val result = authRepository.signIn(email, password)

            result
                .onSuccess {
                    _shouldGoTracker.value = true
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Sign in failed"
                }
        }


    }
}