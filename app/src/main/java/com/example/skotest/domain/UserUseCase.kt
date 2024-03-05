package com.example.skotest.domain

import com.example.skotest.data.api.UserRepository

class UserUseCase(private val userRepository: UserRepository) {
   suspend fun fetchUsers(page: Int, callback: UserRepository.Callback<List<User>>) {
        userRepository.fetchUsers(page, callback)
    }
}
