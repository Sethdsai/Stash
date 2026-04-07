package com.stash.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stash.model.User
import com.stash.service.AuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class StashViewModel(app: Application) : AndroidViewModel(app) {

    private val storageManager = StorageManager(app)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _screen = MutableStateFlow("login")
    val screen: StateFlow<String> = _screen.asStateFlow()

    private val _storageUsed = MutableStateFlow(0L)
    val storageUsed: StateFlow<Long> = _storageUsed.asStateFlow()

    private val _storageLimit = MutableStateFlow(2L * 1024 * 1024 * 1024 * 1024)
    val storageLimit: StateFlow<Long> = _storageLimit.asStateFlow()

    private val _cloudFiles = MutableStateFlow<List<String>>(emptyList())
    val cloudFiles: StateFlow<List<String>> = _cloudFiles.asStateFlow()

    private val _localFiles = MutableStateFlow<List<File>>(emptyList())
    val localFiles: StateFlow<List<File>> = _localFiles.asStateFlow()

    private val _transferStatus = MutableStateFlow("")
    val transferStatus: StateFlow<String> = _transferStatus.asStateFlow()

    private val _transferProgress = MutableStateFlow(0f)
    val transferProgress: StateFlow<Float> = _transferProgress.asStateFlow()

    private val _isAdminUsers = MutableStateFlow<List<String>>(emptyList())
    val adminUsers: StateFlow<List<String>> = _isAdminUsers.asStateFlow()

    private val _loginError = MutableStateFlow("")
    val loginError: StateFlow<String> = _loginError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _adminResponse = MutableStateFlow("")
    val adminResponse: StateFlow<String> = _adminResponse.asStateFlow()

    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage.asStateFlow()

    fun loginWithGitHub(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = ""
            try {
                val user = AuthService.loginWithGitHub(token.trim())
                _user.value = user
                _storageLimit.value = user.storageLimit
                refreshCloudFiles()
                _screen.value = "home"
            } catch (e: Exception) {
                _loginError.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginAsGuest() {
        val user = AuthService.createGuestUser()
        _user.value = user
        _storageLimit.value = user.storageLimit
        refreshCloudFiles()
        _screen.value = "home"
    }

    fun loginWithGoogle(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _loginError.value = "Enter a valid email"
            return
        }
        val user = AuthService.createGoogleUser(email.trim())
        _user.value = user
        _storageLimit.value = user.storageLimit
        refreshCloudFiles()
        _screen.value = "home"
    }

    fun logout() {
        _user.value = null
        _screen.value = "login"
        _loginError.value = ""
        _cloudFiles.value = emptyList()
        _localFiles.value = emptyList()
        _transferStatus.value = ""
        _transferProgress.value = 0f
        _adminResponse.value = ""
        _toastMessage.value = ""
    }

    fun navigateTo(screen: String) {
        _screen.value = screen
    }

    fun clearError() {
        _loginError.value = ""
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2500)
            if (_toastMessage.value == msg) _toastMessage.value = ""
        }
    }

    fun refreshCloudFiles() {
        try {
            _cloudFiles.value = storageManager.getFileNames()
            storageManager.calculateUsedSpace()
            _storageUsed.value = storageManager.usedSpace.value
        } catch (_: Exception) {}
    }

    fun uploadToCloud(name: String, content: String) {
        if (name.isBlank()) return
        try {
            storageManager.addFile(name, content)
            refreshCloudFiles()
            showToast("Uploaded $name")
        } catch (_: Exception) {
            showToast("Upload failed")
        }
    }

    fun deleteCloudFile(name: String) {
        try {
            storageManager.deleteFile(name)
            refreshCloudFiles()
            showToast("Deleted $name")
        } catch (_: Exception) {}
    }

    fun loadLocalFiles() {
        viewModelScope.launch {
            try {
                val downloads = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                )
                val files = downloads.listFiles()?.filter { it.isFile }?.sortedByDescending { it.lastModified() }
                    ?: emptyList()
                _localFiles.value = files
            } catch (_: Exception) {
                _localFiles.value = emptyList()
            }
        }
    }

    fun startTransfer(fileName: String) {
        viewModelScope.launch {
            _transferStatus.value = "Sending $fileName..."
            _transferProgress.value = 0f
            for (i in 1..100) {
                delay(30)
                _transferProgress.value = i / 100f
            }
            _transferStatus.value = "Sent!"
            delay(1000)
            _transferStatus.value = ""
            _transferProgress.value = 0f
        }
    }

    fun loadAdminUsers() {
        _isAdminUsers.value = listOf("Sethdsai (Owner)", "user1", "dev_guy", "coder99")
    }

    fun executeAdminCommand(cmd: String) {
        val lower = cmd.lowercase().trim()
        val response = when {
            lower.startsWith("kick ") -> {
                val target = cmd.substringAfter("kick ").trim()
                "User '$target' has been kicked."
            }
            lower.startsWith("ban ") -> {
                val target = cmd.substringAfter("ban ").trim()
                "User '$target' has been banned."
            }
            lower == "storage info" -> {
                "Owner: 10TB | Users: 2TB\nUsage: ${storageManager.formatBytes(storageManager.usedSpace.value)}\nFiles: ${storageManager.getFileNames().size}"
            }
            lower == "users" -> {
                _isAdminUsers.value.joinToString("\n") { "  - $it" }.let { "Online users:\n$it" }
            }
            lower == "clear logs" -> "Logs cleared."
            lower == "help" -> "kick [user], ban [user], storage info, users, clear logs, help"
            else -> "Unknown command: '$cmd'\nType 'help' for available commands"
        }
        _adminResponse.value = response
    }

    fun getCloudFileSize(name: String): Long {
        return try { storageManager.getFileSize(name) } catch (_: Exception) { 0L }
    }

    fun getCloudFileContent(name: String): String? {
        return try { storageManager.getFileContent(name) } catch (_: Exception) { null }
    }

    fun formatBytes(bytes: Long): String = storageManager.formatBytes(bytes)
}
