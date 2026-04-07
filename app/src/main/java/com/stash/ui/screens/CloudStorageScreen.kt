package com.stash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.Primary
import com.stash.ui.theme.StorageBg
import com.stash.viewmodel.StashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudStorageScreen(viewModel: StashViewModel) {
    val cloudFiles by viewModel.cloudFiles.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()
    val storageLimit by viewModel.storageLimit.collectAsState()
    var showUpload by remember { mutableStateOf(false) }
    var uploadName by remember { mutableStateOf("") }
    var uploadContent by remember { mutableStateOf("") }
    var viewFile by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    val usedFraction = if (storageLimit > 0) storageUsed.toFloat() / storageLimit.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Storage") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showUpload = true },
                containerColor = Primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Upload", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StorageBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cloud Storage", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "${(usedFraction * 100).toInt()}%",
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color(0xFF424242), RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(usedFraction)
                                .height(6.dp)
                                .background(Primary, RoundedCornerShape(3.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${viewModel.formatBytes(storageUsed)} of ${viewModel.formatBytes(storageLimit)}",
                        color = Color(0xFF9E9E9E),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cloudFiles.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFFBDBDBD)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No files in cloud storage", color = Color(0xFF9E9E9E), fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap + to upload", color = Color(0xFFBDBDBD), fontSize = 13.sp)
                }
            } else {
                Text(
                    "${cloudFiles.size} file${if (cloudFiles.size != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(cloudFiles) { fileName ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewFile = fileName },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        fileName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        viewModel.formatBytes(viewModel.getCloudFileSize(fileName)),
                                        fontSize = 12.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                                IconButton(onClick = { deleteTarget = fileName }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (showUpload) {
        AlertDialog(
            onDismissRequest = { showUpload = false },
            title = { Text("Upload File") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uploadName,
                        onValueChange = { uploadName = it },
                        label = { Text("File name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uploadContent,
                        onValueChange = { uploadContent = it },
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (uploadName.isNotBlank()) {
                            viewModel.uploadToCloud(uploadName, uploadContent)
                            uploadName = ""
                            uploadContent = ""
                            showUpload = false
                        }
                    }
                ) {
                    Text("Upload", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpload = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    viewFile?.let { name ->
        val content = viewModel.getCloudFileContent(name)
        AlertDialog(
            onDismissRequest = { viewFile = null },
            title = { Text(name) },
            text = {
                Text(
                    content ?: "Could not read file",
                    fontSize = 13.sp,
                    maxLines = 10
                )
            },
            confirmButton = {
                TextButton(onClick = { viewFile = null }) {
                    Text("Close")
                }
            }
        )
    }

    deleteTarget?.let { name ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete file?") },
            text = { Text("Delete \"$name\" from cloud?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCloudFile(name)
                    deleteTarget = null
                }) {
                    Text("Delete", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
