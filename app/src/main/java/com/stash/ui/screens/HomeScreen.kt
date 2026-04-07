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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Description
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.CloudColor
import com.stash.ui.theme.FileColor
import com.stash.ui.theme.LightGray
import com.stash.ui.theme.MutedText
import com.stash.ui.theme.OwnerGold
import com.stash.ui.theme.Primary
import com.stash.ui.theme.ReceiveColor
import com.stash.ui.theme.SendColor
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
    val displayName = user?.displayName ?: username
    val greeting = if (user?.isOwner == true) "Welcome back, boss" else "Hey, $displayName"
    val usedFraction = if (storageLimit > 0) storageUsed.toFloat() / storageLimit.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Stash", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF1A1A2E))
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(38.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                if (user?.isOwner == true) OwnerGold else UserBlue
                            ),
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
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF546E7A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { AppBottomBar(viewModel, "home") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.requestFilePicker() },
                containerColor = Primary,
                shape = CircleShape,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Pick Files", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(greeting, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "@$username",
                        fontSize = 13.sp,
                        color = MutedText
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (user?.isOwner == true) OwnerGold.copy(alpha = 0.15f) else UserBlue.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (user?.isOwner == true) "\u2B50 OWNER" else "\u2713 PRO",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user?.isOwner == true) Color(0xFFE65100) else UserBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = StorageBg),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = Color(0xFF90CAF9), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Cloud Storage", color = Color(0xFFE0E0E0), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "${viewModel.formatBytes(storageUsed)} / ${viewModel.formatBytes(storageLimit)}",
                            color = Color(0xFF90A4AE),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = usedFraction,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Primary,
                        trackColor = Color(0xFF263238),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(usedFraction * 100).toInt()}% used",
                            color = Color(0xFF78909C),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${viewModel.formatBytes(storageLimit - storageUsed)} free",
                            color = Primary.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Send,
                    label = "Send",
                    subtitle = "Pick & send files",
                    color = SendColor
                ) {
                    viewModel.navigateTo("transfer")
                }
                QuickAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Folder,
                    label = "Receive",
                    subtitle = "Browse device files",
                    color = ReceiveColor
                ) {
                    viewModel.requestFilePicker()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Folder,
                    label = "My Files",
                    subtitle = "Local downloads",
                    color = FileColor
                ) {
                    viewModel.loadLocalFiles()
                    viewModel.navigateTo("files")
                }
                QuickAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Cloud,
                    label = "Cloud",
                    subtitle = "Saved files",
                    color = CloudColor
                ) {
                    viewModel.refreshCloudFiles()
                    viewModel.navigateTo("cloud")
                }
            }

            if (user?.isOwner == true) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.loadAdminUsers()
                            viewModel.navigateTo("admin")
                        }
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = OwnerGold.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = OwnerGold, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Admin Panel", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                            Text("Manage users & storage", fontSize = 12.sp, color = MutedText)
                        }
                        Text("\u203A", fontSize = 24.sp, color = OwnerGold, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (cloudFiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Cloud Files", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Text(
                        "View all",
                        fontSize = 13.sp,
                        color = Primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            viewModel.refreshCloudFiles()
                            viewModel.navigateTo("cloud")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                cloudFiles.take(4).forEach { fileName ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(fileName, fontSize = 14.sp, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            Text(
                                viewModel.formatBytes(viewModel.getCloudFileSize(fileName)),
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAction(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = MutedText)
        }
    }
}

@Composable
fun AppBottomBar(viewModel: StashViewModel, currentRoute: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Default.Home, "Home", "home", currentRoute, viewModel)
            NavItem(Icons.Default.Folder, "Files", "files", currentRoute, viewModel) { viewModel.loadLocalFiles() }
            NavItem(Icons.Default.Cloud, "Cloud", "cloud", currentRoute, viewModel) { viewModel.refreshCloudFiles() }
            NavItem(Icons.Default.Send, "Send", "transfer", currentRoute, viewModel)
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    route: String,
    currentRoute: String,
    viewModel: StashViewModel,
    preNav: (() -> Unit)? = null
) {
    val selected = currentRoute == route
    val color = if (selected) Primary else Color(0xFF90A4AE)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                preNav?.invoke()
                viewModel.navigateTo(route)
            }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .then(if (selected) Modifier.background(Primary.copy(alpha = 0.12f)) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = color, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
