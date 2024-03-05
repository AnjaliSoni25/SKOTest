package com.example.skotest.presentation

import android.content.Context
import androidx.lifecycle.*
import com.example.skotest.data.api.UserRepository
import com.example.skotest.data.db.UserEntity
import com.example.skotest.domain.User
import com.example.skotest.domain.UserUseCase
import kotlinx.coroutines.launch

class UserViewModel(context: Context, private val userUseCase: UserUseCase) : ViewModel() {
    private val _usersLiveData = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> get() = _usersLiveData

    private val _isLoadingLiveData = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingLiveData: LiveData<Boolean> get() = _isLoadingLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData
    var currentPage = 1


    fun fetchUsers() {
        if (!isLoadingLiveData.value!!) {
            _isLoadingLiveData.value = true

            viewModelScope.launch {
                userUseCase.fetchUsers(currentPage, object : UserRepository.Callback<List<User>> {
                    override fun onSuccess(users: List<User>) {
                        _isLoadingLiveData.value = false
                        _usersLiveData.value = users
                        currentPage++ // Increment only when data is successfully fetched
                    }

                    override fun onError(error: String) {
                        _isLoadingLiveData.value = false
                        _errorLiveData.value = error
                    }
                })
            }
        }
    }


}
