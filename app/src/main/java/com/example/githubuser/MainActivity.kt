package com.example.githubuser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuser.databinding.ActivityMainBinding
import com.example.githubuser.helper.ViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.main)
        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter
        val pref = SettingPreferences.getInstance(dataStore)
        val factory = this.application?.let { ViewModelFactory.getInstance(it, pref) }
        mainViewModel = this.let { ViewModelProvider(it, factory!!).get(MainViewModel::class.java) }
        mainViewModel.listItem.observe(this, { listItem ->
            val listItems = ArrayList<UserItems>()
            for (item in listItem) {
                val userItems = UserItems()
                userItems.userName = item.login
                userItems.photo = item.avatarUrl
                listItems.add(userItems)
            }
            adapter.setData(listItems)
            showLoading(false)
        })
        mainViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val intentUserDetail = Intent(this@MainActivity, UserDetailActivity::class.java)
                intentUserDetail.putExtra(UserDetailActivity.USER_DATA, data)
                startActivity(intentUserDetail)
            }
        })
        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(application, pref)).get(
            MainViewModel::class.java
        )
        mainViewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            })

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showLoading(true)
                mainViewModel.findName(query, "main", this@MainActivity)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                showLoading(false)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
            R.id.favorite -> {
                val intentUserFav = Intent(this@MainActivity, FavoriteUserActivity::class.java)
                startActivity(intentUserFav)
            }
        }
    }

}