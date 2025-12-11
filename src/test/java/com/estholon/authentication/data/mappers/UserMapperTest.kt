package com.estholon.authentication.data.mappers

import com.estholon.authentication.data.dtos.UserDto
import com.estholon.authentication.domain.models.UserModel
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {

    private val userMapper = UserMapper()

    @Test
    fun `userDtoToDomain maps fields correctly`() {
        val dto = UserDto(
            uid = "uid123",
            email = "test@example.com",
            displayName = "Test User",
            phoneNumber = "+123456789"
        )

        val result = userMapper.userDtoToDomain(dto)

        assertEquals(dto.uid, result.uid)
        assertEquals(dto.email, result.email)
        assertEquals(dto.displayName, result.displayName)
        assertEquals(dto.phoneNumber, result.phoneNumber)
    }

    @Test
    fun `userDomainToDto maps fields correctly`() {
        val model = UserModel(
            uid = "uid123",
            email = "test@example.com",
            displayName = "Test User",
            phoneNumber = "+123456789"
        )

        val result = userMapper.userDomainToDto(model)

        assertEquals(model.uid, result.uid)
        assertEquals(model.email, result.email)
        assertEquals(model.displayName, result.displayName)
        assertEquals(model.phoneNumber, result.phoneNumber)
    }

    @Test
    fun `FirebaseUser toUserDto extension maps fields correctly`() {
        val firebaseUser = mockk<FirebaseUser>()
        val uid = "uid123"
        val email = "test@example.com"
        val displayName = "Test User"
        val phoneNumber = "+123456789"

        every { firebaseUser.uid } returns uid
        every { firebaseUser.email } returns email
        every { firebaseUser.displayName } returns displayName
        every { firebaseUser.phoneNumber } returns phoneNumber

        val result = firebaseUser.toUserDto()

        assertEquals(uid, result.uid)
        assertEquals(email, result.email)
        assertEquals(displayName, result.displayName)
        assertEquals(phoneNumber, result.phoneNumber)
    }
}
