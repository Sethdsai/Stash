package com.stash.service

import com.stash.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AuthService {

    private const val GITHUB_API = "https://api.github.com/user"
    private val TB: Long = 1024L * 1024L * 1024L * 1024L
    private const val OWNER_GOOGLE = "angoferdinand09@gmail.com"

    suspend fun loginWithGitHub(token: String): User = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(GITHUB_API)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            val code = conn.responseCode
            if (code != 200) {
                val errBody = try { conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "" } catch (_: Exception) { "" }
                throw Exception("GitHub auth failed (HTTP $code). $errBody")
            }

            val body = try { conn.inputStream?.bufferedReader()?.use { it.readText() } ?: "" } catch (_: Exception) { "" }
            if (body.isEmpty()) throw Exception("Empty response from GitHub")

            val json = JSONObject(body)
            val login = json.optString("login", "")
            if (login.isEmpty()) throw Exception("Could not read username from GitHub")

            val name = json.optString("name", login).ifEmpty { login }
            val createdAt = json.optString("created_at", "").ifEmpty { "Unknown" }.let { if (it.length >= 10) it.substring(0, 10) else it }
            val isOwner = login.equals("Sethdsai", ignoreCase = true)

            User(
                username = login,
                isOwner = isOwner,
                storageLimit = if (isOwner) 10L * TB else 2L * TB,
                storageUsed = 0L,
                joinDate = createdAt,
                displayName = name,
                loginMethod = "github"
            )
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            throw Exception("Login failed: ${e.message}")
        } finally {
            conn?.disconnect()
        }
    }

    fun createGuestUser(): User {
        return User(
            username = "Guest",
            isOwner = false,
            storageLimit = 2L * TB,
            storageUsed = 0L,
            joinDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            displayName = "Guest",
            loginMethod = "guest"
        )
    }

    fun createGoogleUser(email: String): User {
        val isOwner = email.equals(OWNER_GOOGLE, ignoreCase = true)
        val name = email.substringBefore("@").replace(".", " ").split(" ").joinToString(" ") { w ->
            w.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
        }
        return User(
            username = email.substringBefore("@"),
            isOwner = isOwner,
            storageLimit = if (isOwner) 10L * TB else 2L * TB,
            storageUsed = 0L,
            joinDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            displayName = name,
            loginMethod = "google"
        )
    }
}
