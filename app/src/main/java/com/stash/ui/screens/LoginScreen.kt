package com.stash.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stash.ui.theme.Primary
import com.stash.ui.theme.PrimaryLight
import com.stash.ui.theme.Secondary
import com.stash.viewmodel.StashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: StashViewModel) {
    var activeTab by remember { mutableStateOf("github") }
    var token by remember { mutableStateOf("") }
    var showToken by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }
    var showTokenHelp by remember { mutableStateOf(false) }
    val loginError by viewModel.loginError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stash",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Transfer. Store. Share.",
            fontSize = 14.sp,
            color = Color(0xFF78909C),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Secondary)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabChip("GitHub", activeTab == "github") { activeTab = "github"; viewModel.clearError() }
                    TabChip("Google", activeTab == "google") { activeTab = "google"; viewModel.clearError() }
                    TabChip("Guest", activeTab == "guest") { activeTab = "guest"; viewModel.clearError() }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (activeTab) {
                    "github" -> GitHubLoginTab(
                        token, showToken, isLoading, loginError, showTokenHelp,
                        { token = it }, { showToken = !showToken },
                        { viewModel.loginWithGitHub(token.trim()) },
                        { showTokenHelp = !showTokenHelp }
                    )
                    "google" -> GoogleLoginTab(
                        googleEmail, loginError,
                        { googleEmail = it },
                        { viewModel.loginWithGoogle(googleEmail) }
                    )
                    "guest" -> GuestLoginTab { viewModel.loginAsGuest() }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "By continuing you agree to our Terms of Service",
            fontSize = 12.sp,
            color = Color(0xFFB0BEC5),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun TabChip(label: String, active: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .then(if (active) Modifier.background(Color.White) else Modifier)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) Primary else Color(0xFF90A4AE)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GitHubLoginTab(
    token: String,
    showToken: Boolean,
    isLoading: Boolean,
    loginError: String,
    showTokenHelp: Boolean,
    onTokenChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onLogin: () -> Unit,
    onToggleHelp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChange,
            label = { Text("GitHub Token") },
            placeholder = { Text("ghp_xxxxxxxxxxxx") },
            singleLine = true,
            visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle"
                    )
                }
            },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF90A4AE))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        TextButton(onClick = onToggleHelp) {
            Text("How to get a token?", fontSize = 12.sp, color = Color(0xFF78909C))
        }

        if (showTokenHelp) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Go to github.com \u2192 Settings \u2192 Developer settings \u2192 Personal access tokens \u2192 Generate new token (classic). Give it read:user scope.",
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037),
                    modifier = Modifier.padding(14.dp),
                    lineHeight = 18.sp
                )
            }
        }

        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = loginError,
                    color = Color(0xFFC62828),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E), contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            enabled = token.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else {
                Text("Login with GitHub", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoogleLoginTab(
    email: String,
    loginError: String,
    onEmailChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign in with your Google account",
            fontSize = 13.sp,
            color = Color(0xFF78909C),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Google Email") },
            placeholder = { Text("you@gmail.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = loginError,
                    color = Color(0xFFC62828),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF333333)),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google", fontSize = 15.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun GuestLoginTab(onLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No account needed",
            fontSize = 13.sp,
            color = Color(0xFF78909C),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Get 2 TB of free cloud storage instantly",
            fontSize = 15.sp,
            color = Primary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, Primary)
        ) {
            Text("Continue as Guest", fontSize = 15.sp, color = Primary, fontWeight = FontWeight.Medium)
        }
    }
}
