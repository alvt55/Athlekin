package com.example.athlekin.ui.signup

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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    fun signUp(
        email: String,
        password: String,
        repeatPassword: String,
//        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
//        if (!email.isValidEmail()) {
//            showErrorSnackbar(ErrorMessage.IdError(R.string.invalid_email))
//            return
//        }
//
//        if (!password.isValidPassword()) {
//            showErrorSnackbar(ErrorMessage.IdError(R.string.invalid_password))
//            return
//        }
//
//        if (password != repeatPassword) {
//            showErrorSnackbar(ErrorMessage.IdError(R.string.passwords_do_not_match))
//            return
//        }

        viewModelScope.launch{
            authRepository.signUp(email, password)
            _shouldRestartApp.value = true
        }


    }
}