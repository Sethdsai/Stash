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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.Error
import com.stash.ui.theme.LightGray
import com.stash.ui.theme.MutedText
import com.stash.ui.theme.OwnerGold
import com.stash.ui.theme.Primary
import com.stash.ui.theme.StorageBg
import com.stash.viewmodel.StashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: StashViewModel) {
    val user by viewModel.user.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()
    val storageLimit by viewModel.storageLimit.collectAsState()

    val usedFraction = if (storageLimit > 0) storageUsed.toFloat() / storageLimit.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E)) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .shadow(4.dp, CircleShape)
                                .background(if (user?.isOwner == true) OwnerGold else Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.displayName ?: user?.username ?: "?").take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user?.displayName ?: user?.username ?: "Unknown", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                            Text("@${user?.username ?: ""}", fontSize = 13.sp, color = MutedText)
                        }
                        val methodLabel = when (user?.loginMethod) {
                            "guest" -> "Guest"
                            "google" -> "Google"
                            else -> "GitHub"
                        }
                        val methodColor = when (user?.loginMethod) {
                            "guest" -> Color(0xFF78909C)
                            "google" -> Color(0xFF4285F4)
                            else -> Color(0xFF24292E)
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightGray),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = methodLabel,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = methodColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsRow("Username", "@${user?.username ?: "-"}")
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingsRow("Role", if (user?.isOwner == true) "Owner" else "User")
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingsRow("Joined", user?.joinDate ?: "-")
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingsRow("Storage", viewModel.formatBytes(storageLimit))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Storage Usage", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = StorageBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Used", fontSize = 14.sp, color = Color(0xFFE0E0E0))
                        Text(
                            "${viewModel.formatBytes(storageUsed)} / ${viewModel.formatBytes(storageLimit)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = usedFraction,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Primary,
                        trackColor = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${(usedFraction * 100).toInt()}% used", fontSize = 12.sp, color = Color(0xFF78909C))
                        Text("${viewModel.formatBytes(storageLimit - storageUsed)} free", fontSize = 12.sp, color = Primary.copy(alpha = 0.8f))
                    }
                }
            }

            if (user?.isOwner == true) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Owner", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.loadAdminUsers()
                            viewModel.navigateTo("admin")
                        }
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = OwnerGold.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = OwnerGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Admin Panel", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E), modifier = Modifier.weight(1f))
                        Text("\u203A", fontSize = 22.sp, color = OwnerGold, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("About", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedText, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsRow("Version", "1.1")
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Stash is a file transfer and cloud storage app. Send files between devices, manage local files, and store documents in the cloud.",
                            fontSize = 13.sp,
                            color = MutedText,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Error),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MutedText)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
    }
}
