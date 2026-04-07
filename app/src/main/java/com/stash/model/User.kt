package com.stash.model

data class User(
    val username: String,
    val isOwner: Boolean,
    val storageLimit: Long,
    val storageUsed: Long,
    val joinDate: String,
    val displayName: String,
    val loginMethod: String = "github"
)
