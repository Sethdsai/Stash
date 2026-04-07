package com.stash

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                val pickerCount by viewModel.pickerCounter.collectAsState()
                val toast by viewModel.toastMessage.collectAsState()

                val filePicker = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetMultipleContents()
                ) { uris: List<Uri> ->
                    viewModel.onFilesPicked(uris)
                }

                LaunchedEffect(pickerCount) {
                    if (pickerCount > 0) {
                        try {
                            filePicker.launch("*/*")
                        } catch (_: Exception) {}
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
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

                    if (toast.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 100.dp, start = 32.dp, end = 32.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = toast,
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
