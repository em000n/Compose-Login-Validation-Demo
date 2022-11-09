package com.emon.composeloginvalidationdemo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val ioValidation: IoValidation = IoValidation()
) : BaseViewModel() {
    val action: (LoginUiAction) -> Unit

    var state by mutableStateOf(Param())
    var uiState by mutableStateOf(LoginUiState.Loading(false))

    private val _uiStateEvent = Channel<LoginUiStateEvent<Any>>()
    val uiStateEvent get() = _uiStateEvent.receiveAsFlow()

    init {
        action = {
            when (it) {
                is LoginUiAction.Login -> login()
                is LoginUiAction.EmailChanged -> {
                    state = state.copy(email = it.params)
                    state = state.copy(emailError = null)
                }
                is LoginUiAction.PasswordChanged -> {
                    state = state.copy(password = it.params)
                    state = state.copy(passwordError = null)
                }
            }
        }
    }

    private fun login() {
        execute {
            when (val result = ioValidation.loginIoValidation(state)) {
                is ValidationResult.Failure<*> -> {
                    when (result.ioErrorResult) {
                        is LoginIoResult.EmailError -> state =
                            state.copy(emailError = "Invalid Email")
                        is LoginIoResult.PasswordError -> state =
                            state.copy(passwordError = "Invalid Password")
                    }
                }
                is ValidationResult.Success -> {
                    uiState = uiState.copy(loading = true)
                    viewModelScope.launch {
                        delay(5000)
                        _uiStateEvent.send(LoginUiStateEvent.LoginSuccess("Login Success"))

                        uiState = uiState.copy(loading = false)
                        /* state = state.copy(
                             email = "",
                             password = "",
                             emailError = null,
                             passwordError = null
                         )*/
                    }
                }
            }
        }
    }

    data class Param(
        val email: String = "",
        val password: String = "",
        val emailError: String? = null,
        val passwordError: String? = null,
    )

    sealed class LoginUiState<out R> {
        data class Loading(var loading: Boolean) : LoginUiState<Loading>()
    }

    sealed class LoginUiStateEvent<out R> {
        data class LoginSuccess(var message: String) : LoginUiStateEvent<Any>()
    }

    sealed class LoginUiAction {
        object Login : LoginUiAction()
        data class EmailChanged(val params: String) : LoginUiAction()
        data class PasswordChanged(val params: String) : LoginUiAction()
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Failure<T>(val ioErrorResult: T) : ValidationResult()
    }

    sealed class LoginIoResult {
        object PasswordError : LoginIoResult()
        object EmailError : LoginIoResult()
    }
}