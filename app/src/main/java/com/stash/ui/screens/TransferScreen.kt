package com.stash.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.CloudColor
import com.stash.ui.theme.LightGray
import com.stash.ui.theme.MutedText
import com.stash.ui.theme.Primary
import com.stash.ui.theme.ReceiveColor
import com.stash.ui.theme.SendColor
import com.stash.ui.theme.Success
import com.stash.viewmodel.StashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(viewModel: StashViewModel) {
    val transferStatus by viewModel.transferStatus.collectAsState()
    val transferProgress by viewModel.transferProgress.collectAsState()
    val pickedFiles by viewModel.pickedFiles.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = transferProgress,
        label = "transfer"
    )
    val isComplete = transferStatus.contains("Sent!")
    val isTransferring = transferStatus.isNotEmpty() && !isComplete

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send & Receive", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { AppBottomBar(viewModel, "transfer") },
        floatingActionButton = {
            if (!isTransferring && !isComplete) {
                FloatingActionButton(
                    onClick = { viewModel.requestFilePicker() },
                    containerColor = Primary,
                    shape = CircleShape,
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.InsertDriveFile, contentDescription = "Pick Files", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (isComplete) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(72.dp), tint = Success)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(transferStatus, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Success)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Transfer completed", fontSize = 14.sp, color = MutedText)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { viewModel.navigateTo("home") },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Back to Home", color = Primary)
                    }
                }
            } else if (isTransferring) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.size(120.dp),
                        strokeWidth = 8.dp,
                        color = Primary,
                        trackColor = LightGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(transferStatus, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${(animatedProgress * 100).toInt()}%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            } else if (pickedFiles.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(56.dp), tint = MutedText)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No files selected", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tap the + button to browse your device storage, apps, and files", fontSize = 13.sp, color = MutedText, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.requestFilePicker() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SendColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pick Files", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${pickedFiles.size} file${if (pickedFiles.size != 1) "s" else ""} selected", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E), modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.clearPickedFiles() }) {
                        Text("Clear all", fontSize = 13.sp, color = Color(0xFFE53935))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (pickedFiles.size > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.sendAllPicked() },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SendColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send All", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { viewModel.saveAllPickedToCloud() },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CloudColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Cloud", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(pickedFiles) { index, file ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SendColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = SendColor, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        file.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1A1A2E),
                                        maxLines = 1
                                    )
                                    Text(
                                        viewModel.formatBytes(file.size),
                                        fontSize = 12.sp,
                                        color = MutedText
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.startTransfer(file.name) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SendColor)
                                ) {
                                    Text("Send", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedButton(
                                    onClick = { viewModel.savePickedFileToCloud(index) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CloudColor)
                                ) {
                                    Text("Cloud", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = { viewModel.removePickedFile(index) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFFBDBDBD), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}
