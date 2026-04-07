package com.stash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.stash.ui.screens.AdminScreen
import com.stash.ui.screens.CloudStorageScreen
import com.stash.ui.screens.FileManagerScreen
import com.stash.ui.screens.HomeScreen
import com.stash.ui.screens.LoginScreen
import com.stash.ui.screens.SettingsScreen
import com.stash.ui.screens.TransferScreen
import com.stash.ui.theme.StashTheme
import com.stash.viewmodel.StashViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: StashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = StashViewModel(application)

        setContent {
            StashTheme {
                val screen by viewModel.screen.collectAsState()

                when (screen) {
                    "login" -> LoginScreen(viewModel)
                    "home" -> HomeScreen(viewModel)
                    "files" -> FileManagerScreen(viewModel)
                    "cloud" -> CloudStorageScreen(viewModel)
                    "transfer" -> TransferScreen(viewModel)
                    "admin" -> AdminScreen(viewModel)
                    "settings" -> SettingsScreen(viewModel)
                    else -> LoginScreen(viewModel)
                }
            }
        }
    }
}
