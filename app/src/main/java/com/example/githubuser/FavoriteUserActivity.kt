package com.example.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.ActivityFavoriteUserBinding
import com.example.githubuser.helper.ViewModelFactory

class FavoriteUserActivity : AppCompatActivity() {
    private var _activityFavoriteUserBinding: ActivityFavoriteUserBinding? = null
    private val binding get() = _activityFavoriteUserBinding
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityFavoriteUserBinding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.title = getString(R.string.favorite)
        val mainViewModel = obtainViewModel(this@FavoriteUserActivity)
        mainViewModel.getAllFav().observe(this, { favList ->
            val listItems = ArrayList<UserItems>()
            if (favList != null && favList.isNotEmpty()) {
                for (i in favList.indices) {
                    val userItems = UserItems()
                    userItems.userName = favList[i].username
                    userItems.photo = favList[i].photo
                    listItems.add(userItems)
                }
            }
            adapter.setData(listItems)
        })

        adapter = UserAdapter()

        binding?.rvUserFav?.layoutManager = LinearLayoutManager(this)
        binding?.rvUserFav?.setHasFixedSize(true)
        binding?.rvUserFav?.adapter = adapter
        binding?.rvUserFav?.setOnClickListener { view ->
            Toast.makeText(this, "tesss", Toast.LENGTH_SHORT).show()
        }
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val intentUserDetail =
                    Intent(this@FavoriteUserActivity, UserDetailActivity::class.java)
                intentUserDetail.putExtra(UserDetailActivity.USER_DATA, data)
                startActivity(intentUserDetail)
            }
        })
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val pref = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityFavoriteUserBinding = null
    }
}