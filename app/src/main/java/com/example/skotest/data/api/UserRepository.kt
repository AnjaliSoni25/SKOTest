package com.example.skotest.data.api

import android.util.Log
import com.example.skotest.data.db.UserDao
import com.example.skotest.data.db.UserEntity
import com.example.skotest.domain.User
import com.example.skotest.domain.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

class UserRepository(private val userDao: UserDao, private val apiService: ApiService) {
    interface Callback<T> {
        fun onSuccess(data: T)
        fun onError(error: String)
    }

   suspend fun fetchUsers(page: Int, callback: Callback<List<User>>) {
       try {
           val response = withContext(Dispatchers.IO) {
               apiService.getUsers(page).execute()
           }
           Log.d("API_", "Code: ${response.code()}")

           if (response.isSuccessful) {
               val userResponse = response.body()
               val users = userResponse?.data.orEmpty()
               val usersFromApi: List<User> = users

               // Convert API users to database entities
               val dbEntities = usersFromApi.map { mapApiUserToEntity(it) }

               // Filter out users with null first names
               val validUsers = dbEntities.filter { it.firstName != null }
               // Insert or update data in the database using a coroutine
               withContext(Dispatchers.IO) {
                   userDao.insertUsers(validUsers)
               }

               // Retrieve data from the database and callback
               val usersFromDB = withContext(Dispatchers.IO) {
                   userDao.getAllUsers()
               }

               callback.onSuccess(usersFromApi)
           } else {
               val errorMessage = response.errorBody()?.string() ?: "Unknown error"
               callback.onError("Error: ${response.code()}, Message: $errorMessage")
           }
       } catch (e: Exception) {
           val errorMessage = e.message ?: "Unknown exception"
           callback.onError("Network error: $errorMessage")
       }
   }


    suspend fun getAllUsersFromDB(): List<UserEntity> {
        return userDao.getAllUsers()
    }


    private fun mapApiUserToEntity(apiUser: User): UserEntity {
        return UserEntity(
            id = apiUser.id,
            email = apiUser.email,
            firstName = apiUser.first_name,
            lastName = apiUser.last_name,
            avatar = apiUser.avatar
        )
    }
}

