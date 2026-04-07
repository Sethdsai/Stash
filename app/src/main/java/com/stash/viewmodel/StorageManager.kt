package com.stash.viewmodel

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class StorageManager(context: Context) {

    companion object {
        private val TB: Long = 1024L * 1024L * 1024L * 1024L
        val TOTAL_OWNER_STORAGE = 10L * TB
        val TOTAL_USER_STORAGE = 2L * TB
    }

    val filesDir = File(context.filesDir, "stash_cloud")

    init {
        if (!filesDir.exists()) filesDir.mkdirs()
    }

    private val _usedSpace = MutableStateFlow(0L)
    val usedSpace: StateFlow<Long> = _usedSpace.asStateFlow()

    fun addFile(name: String, content: String): Boolean {
        return try {
            val file = File(filesDir, name)
            file.writeText(content)
            calculateUsedSpace()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun addFileFromUri(context: Context, uri: Uri, name: String): Boolean {
        return try {
            val file = File(filesDir, name)
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return false
            calculateUsedSpace()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun getFileContent(name: String): String? {
        val file = File(filesDir, name)
        return if (file.exists()) file.readText() else null
    }

    fun deleteFile(name: String): Boolean {
        val file = File(filesDir, name)
        val deleted = file.delete()
        if (deleted) calculateUsedSpace()
        return deleted
    }

    fun getFileNames(): List<String> {
        return filesDir.listFiles()?.map { it.name }?.sorted() ?: emptyList()
    }

    fun getFileSize(name: String): Long {
        val file = File(filesDir, name)
        return if (file.exists()) file.length() else 0L
    }

    fun calculateUsedSpace() {
        var total = 0L
        filesDir.listFiles()?.forEach { total += it.length() }
        _usedSpace.value = total
    }

    fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024
        if (kb < 1024) return "$kb KB"
        val mb = kb / 1024
        if (mb < 1024) return "$mb MB"
        val gb = mb / 1024
        if (gb < 1024) return "$gb GB"
        val tb = gb / 1024
        return "$tb TB"
    }
}
