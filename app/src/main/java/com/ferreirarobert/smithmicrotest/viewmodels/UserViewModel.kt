package com.ferreirarobert.smithmicrotest.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreirarobert.smithmicrotest.api.FirestoreRepository
import com.ferreirarobert.smithmicrotest.models.NetworkError
import com.ferreirarobert.smithmicrotest.models.User
import com.ferreirarobert.smithmicrotest.repositories.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    fun saveUsername(user: User) {
        viewModelScope.launch {
             userDataStore.saveUsername(user.username)
            _user.value = user.username
        }
    }

    private var _showRegister = MutableStateFlow(false)
    val showRegister: StateFlow<Boolean> = _showRegister

    private var _user = MutableStateFlow<String?>(null)
    val user: StateFlow<String?> = _user

    private var _postSuccess = MutableStateFlow<Boolean?>(null)
    val postSuccess: StateFlow<Boolean?> = _postSuccess

    private var _regUserError = MutableStateFlow<Boolean?>(null)
    val regUserError: StateFlow<Boolean?> = _regUserError

    private var _regPassError = MutableStateFlow<Boolean?>(null)
    val regPassError: StateFlow<Boolean?> = _regPassError

    private var _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess: StateFlow<Boolean?> = _loginSuccess

    fun setShowRegister(value: Boolean) {
        _showRegister.value = value
    }

    fun postUser(username: String, password: String) {
        if (username.isEmpty()) {
            _postSuccess.value = false
            _errorMessage.value = "Empty username"
            _regUserError.value = false
            return
        }
        if (password.isEmpty()) {
            _postSuccess.value = false
            _errorMessage.value = "Empty password"
            _regPassError.value = false
            return
        }

        val newUser = User(username, password)
        viewModelScope.launch {
            repository.postUser(
                user = newUser,
                onUserPosted = { message ->
                    _regUserError.value = false
                    _regPassError.value = false
                    _postSuccess.value = true
                    _errorMessage.value = null
                    _showRegister.value = false
                },
                onError = { error, exception ->
                    val errMsg = NetworkError.returnMessage(error)

                    if (error == NetworkError.USER_ALREADY_EXIST) {
                        _regUserError.value = true
                    } else {
                        _regUserError.value = false
                    }
                    Log.e(TAG, errMsg, exception)
                    _postSuccess.value = false
                    _errorMessage.value = errMsg
                }
            )
        }
    }

    fun getUser(username: String, password: String) {
        viewModelScope.launch {
            repository.getUser(
                username = username,
                password = password,
                onUserReady = { user ->
                    saveUsername(user)
                    _loginSuccess.value = true
                    _errorMessage.value = null
                },
                onError = { error, exception ->
                    _errorMessage.value = NetworkError.returnMessage(error)
                    Log.e(TAG, _errorMessage.value, exception)
                    _loginSuccess.value = false
                }
            )
        }
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = null
        _user.value = null
    }

    fun resetError() {
        _errorMessage.value = null
    }

    companion object {
        private const val TAG = "User"
    }
}