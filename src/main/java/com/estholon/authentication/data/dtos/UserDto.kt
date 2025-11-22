package com.estholon.authentication.data.dtos

data class UserDto(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val phoneNumber: String?
)