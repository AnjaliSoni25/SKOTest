package com.example.skotest.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.skotest.R
import com.example.skotest.data.api.RetrofitClient
import com.example.skotest.data.api.UserRepository
import com.example.skotest.data.db.AppDatabase
import com.example.skotest.databinding.ActivityMainBinding
import com.example.skotest.domain.UserUseCase


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userAdapter : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setDbInstance()

         userAdapter = UserAdapter(this, emptyList())

        observeSuccess()
        observeError()
        initRecyclerView(userAdapter)
        observeLoading()
        pagination()


        // Fetch users when the activity is created
        userViewModel.fetchUsers()
    }

    private fun observeSuccess() {
        userViewModel.usersLiveData.observe(this) { data ->
            userAdapter.updateUsers(data)
            Toast.makeText(this, "Success: ${data.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pagination() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!userViewModel.isLoadingLiveData.value!! && firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                    userViewModel.fetchUsers()
                }
            }
        })
    }

    private fun observeLoading() {
        userViewModel.isLoadingLiveData.observe(this) { isLoading ->
            binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeError() {
        userViewModel.errorLiveData.observe(this) { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDbInstance() {
        val roomDatabase = AppDatabase.getInstance(this)
        val userDao = roomDatabase.userDao()
        val userRepository = UserRepository(userDao, RetrofitClient.getApiService())
        val userUseCase = UserUseCase(userRepository)
        userViewModel = UserViewModel(this, userUseCase)
        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this // This is important for the LiveData to be observed in the XML layout
    }

    private fun initRecyclerView(userAdapter: UserAdapter) {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

    }
}
