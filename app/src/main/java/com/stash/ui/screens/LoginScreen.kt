package com.stash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Stash",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "file transfer & cloud storage",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF5F5F5))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabChip("GitHub", activeTab == "github") { activeTab = "github"; viewModel.clearError() }
            TabChip("Google", activeTab == "google") { activeTab = "google"; viewModel.clearError() }
            TabChip("Guest", activeTab == "guest") { activeTab = "guest"; viewModel.clearError() }
        }

        Spacer(modifier = Modifier.height(28.dp))

        when (activeTab) {
            "github" -> GitHubLoginTab(
                token = token,
                showToken = showToken,
                isLoading = isLoading,
                loginError = loginError,
                showTokenHelp = showTokenHelp,
                onTokenChange = { token = it },
                onToggleVisibility = { showToken = !showToken },
                onLogin = { viewModel.loginWithGitHub(token.trim()) },
                onToggleHelp = { showTokenHelp = !showTokenHelp }
            )
            "google" -> GoogleLoginTab(
                email = googleEmail,
                loginError = loginError,
                onEmailChange = { googleEmail = it },
                onLogin = { viewModel.loginWithGoogle(googleEmail) }
            )
            "guest" -> GuestLoginTab(onLogin = { viewModel.loginAsGuest() })
        }
    }
}

@Composable
private fun TabChip(label: String, active: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (active) Modifier.background(Color.White)
                else Modifier
            )
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) Primary else Color(0xFF9E9E9E)
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
            label = { Text("GitHub Personal Access Token") },
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
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9E9E9E))
            },
            modifier = Modifier.fillMaxWidth()
        )

        TextButton(onClick = onToggleHelp) {
            Text("How to get a token?", fontSize = 13.sp, color = Color(0xFF9E9E9E))
        }

        if (showTokenHelp) {
            Spacer(modifier = Modifier.height(4.dp))
            CardHelpContent(
                text = "Go to github.com → Settings → Developer settings → Personal access tokens → Generate new token. Give it 'read:user' scope."
            )
        }

        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginError,
                color = Color(0xFFD32F2F),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E), contentColor = Color.White),
            enabled = token.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Text("Login with GitHub", fontSize = 16.sp)
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
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Google Email") },
            placeholder = { Text("you@gmail.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginError,
                color = Color(0xFFD32F2F),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF333333)),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Sign in with Google", fontSize = 16.sp, color = Color(0xFF333333))
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
            text = "Continue without an account",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You'll get 2 TB of free cloud storage",
            fontSize = 13.sp,
            color = Color(0xFF9E9E9E)
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
        ) {
            Text("Continue as Guest", fontSize = 16.sp, color = Primary)
        }
    }
}

@Composable
private fun CardHelpContent(text: String) {
    androidx.compose.material3.Card(
        shape = RoundedCornerShape(10.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF795548),
            modifier = Modifier.padding(14.dp),
            lineHeight = 18.sp
        )
    }
}
