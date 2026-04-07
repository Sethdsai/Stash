package com.stash.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.Primary
import com.stash.ui.theme.Success
import com.stash.viewmodel.StashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(viewModel: StashViewModel) {
    val transferStatus by viewModel.transferStatus.collectAsState()
    val transferProgress by viewModel.transferProgress.collectAsState()
    val localFiles by viewModel.localFiles.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = transferProgress,
        label = "transfer"
    )
    val isComplete = transferStatus == "Sent!"
    val isTransferring = transferStatus.isNotEmpty() && !isComplete

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send File") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (localFiles.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFFBDBDBD)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No files to send", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Go to My Files to browse and send",
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            } else if (isComplete) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = Success
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("File sent!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Success)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Transfer completed successfully",
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E)
                )
            } else if (isTransferring) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(120.dp),
                    strokeWidth = 8.dp,
                    color = Primary,
                    trackColor = Color(0xFFF5F5F5)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(transferStatus, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${(animatedProgress * 100).toInt()}%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFFBDBDBD)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Select a file to send", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Browse your files and tap the send icon",
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "${localFiles.size} files available",
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}
