package com.boutaina.todo.user

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutaina.todo.data.Api
import com.boutaina.todo.data.UserUpdate
import com.boutaina.todo.data.Commands
import com.boutaina.todo.utils.toRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.boutaina.todo.auth.TokenRepository

class UserViewModel : ViewModel() {

    private val webService = Api.userWebService

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> get() = _userName

    private val _userAvatarUri = MutableStateFlow<Uri?>(null)
    val userAvatarUri: StateFlow<Uri?> get() = _userAvatarUri

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> get() = _isUploading

    // Fetch user information from API
    fun fetchUser() {
        viewModelScope.launch {
            try {
                val response = webService.fetchUser()
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        _userName.value = it.name
                        _userAvatarUri.value = Uri.parse(it.avatar) // assuming avatar is a URL
                    } ?: run {
                        _userName.value = "Utilisateur inconnu"
                    }
                } else {
                    _userName.value = "Erreur: ${response.code()}"
                }
            } catch (e: Exception) {
                _userName.value = "Erreur de chargement"
                Log.e("UserViewModel", "Exception: ${e.localizedMessage}", e)
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            try {
                val userUpdate = UserUpdate(newName)
                val commands = Commands(user_update = userUpdate)

                val response = webService.update(commands)
                if (response.isSuccessful) {
                    _userName.value = newName
                    println("User name updated successfully!")
                } else {
                    println("Error updating user name: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadAvatar(uri: Uri, context: Context) {
        _isUploading.value = true
        viewModelScope.launch {
            try {
                val requestBody = uri.toRequestBody(context)
                val response = webService.updateAvatar(requestBody)
                if (response.isSuccessful) {
                    println("Avatar updated successfully!")
                } else {
                    println("Error updating avatar: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isUploading.value = false
            }
        }
    }
}
