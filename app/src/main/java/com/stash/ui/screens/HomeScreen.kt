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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.OwnerGold
import com.stash.ui.theme.Primary
import com.stash.ui.theme.StorageBg
import com.stash.ui.theme.UserBlue
import com.stash.viewmodel.StashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: StashViewModel) {
    val user by viewModel.user.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()
    val storageLimit by viewModel.storageLimit.collectAsState()
    val cloudFiles by viewModel.cloudFiles.collectAsState()

    val username = user?.username ?: "User"
    val greeting = if (user?.isOwner == true) "Hey boss" else "Hey $username"
    val usedFraction = if (storageLimit > 0) storageUsed.toFloat() / storageLimit.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Stash", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = { viewModel.navigateTo("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(greeting, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (user?.isOwner == true) OwnerGold else UserBlue
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (user?.isOwner == true) "OWNER" else "PRO",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StorageBg),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Storage", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${viewModel.formatBytes(storageUsed)} / ${viewModel.formatBytes(storageLimit)}",
                            color = Color(0xFFBDBDBD),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = usedFraction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Primary,
                        trackColor = Color(0xFF424242),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(usedFraction * 100).toInt()}% used",
                        color = Color(0xFF9E9E9E),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Send,
                    label = "Send Files",
                    color = Color(0xFFFFCC80)
                ) { viewModel.navigateTo("transfer") }
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WifiTethering,
                    label = "Receive",
                    color = Color(0xFFA5D6A7)
                ) { viewModel.navigateTo("transfer") }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Folder,
                    label = "My Files",
                    color = Color(0xFF90CAF9)
                ) {
                    viewModel.loadLocalFiles()
                    viewModel.navigateTo("files")
                }
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Cloud,
                    label = "Cloud Storage",
                    color = Color(0xFFFFAB91)
                ) {
                    viewModel.refreshCloudFiles()
                    viewModel.navigateTo("cloud")
                }
            }

            if (user?.isOwner == true) {
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Security,
                    label = "Admin Panel",
                    color = OwnerGold
                ) {
                    viewModel.loadAdminUsers()
                    viewModel.navigateTo("admin")
                }
            }

            if (cloudFiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Files", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "View All",
                        fontSize = 14.sp,
                        color = Primary,
                        modifier = Modifier.clickable {
                            viewModel.refreshCloudFiles()
                            viewModel.navigateTo("cloud")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                cloudFiles.take(3).forEach { fileName ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = Primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(fileName, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(
                                viewModel.formatBytes(viewModel.getCloudFileSize(fileName)),
                                fontSize = 12.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
