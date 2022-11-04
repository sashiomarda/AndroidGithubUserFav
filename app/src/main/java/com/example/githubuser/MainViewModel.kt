package com.example.githubuser

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuser.database.Fav
import com.example.githubuser.repository.FavRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val pref: SettingPreferences) : ViewModel() {

    private val _listItem = MutableLiveData<List<ItemsItem>>()
    val listItem: LiveData<List<ItemsItem>> = _listItem

    private val _listItemFoll = MutableLiveData<List<ResponseUserFoll>>()
    val listItemFoll: LiveData<List<ResponseUserFoll>> = _listItemFoll

    private val _urlPhoto = MutableLiveData<String>()
    val urlPhoto: LiveData<String> = _urlPhoto

    private val _name = MutableLiveData<String>()
    val name: LiveData<String?> = _name

    private val _follower = MutableLiveData<Int>()
    val follower: LiveData<Int> = _follower

    private val _following = MutableLiveData<Int>()
    val following: LiveData<Int> = _following

    private val _company = MutableLiveData<String>()
    val company: LiveData<String> = _company

    private val _repo = MutableLiveData<Int>()
    val repo: LiveData<Int> = _repo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val mFavRepository: FavRepository = FavRepository(application)

    fun getAllFav(): LiveData<List<Fav>> = mFavRepository.getAllFav()

    fun checkIfExists(mUserName: String): LiveData<List<Fav>> =
        mFavRepository.checkIfExist(mUserName)

    fun findName(mName: String?, list: String, mContext: Context) {
        _isLoading.value = true
        when (list) {
            "main" -> getMainApi(mName, mContext)
            "follower" -> getFollowerApi(mName, mContext)
            "following" -> getFollowingApi(mName, mContext)
        }

    }

    private fun getFollowingApi(mName: String?, mContext: Context) {
        val client = ApiConfig.getApiService(ApiConfig.ApiOrigin.DETAIL).getFollowing(mName)
        client.enqueue(object : Callback<List<ResponseUserFoll>> {
            override fun onResponse(
                call: Call<List<ResponseUserFoll>>,
                response: Response<List<ResponseUserFoll>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listItemFoll.value = response.body()!!
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ResponseUserFoll>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(mContext, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFollowerApi(mName: String?, mContext: Context) {
        val client = ApiConfig.getApiService(ApiConfig.ApiOrigin.DETAIL).getFollower(mName)
        client.enqueue(object : Callback<List<ResponseUserFoll>> {
            override fun onResponse(
                call: Call<List<ResponseUserFoll>>,
                response: Response<List<ResponseUserFoll>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listItemFoll.value = response.body()!!
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ResponseUserFoll>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(mContext, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getMainApi(mName: String?, mContext: Context) {
        val client = ApiConfig.getApiService(ApiConfig.ApiOrigin.MAIN).getName(mName)
        client.enqueue(object : Callback<ResponseUserSearch> {
            override fun onResponse(
                call: Call<ResponseUserSearch>,
                response: Response<ResponseUserSearch>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listItem.value = response.body()?.items
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseUserSearch>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(mContext, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getDetailUser(mName: String, mContext: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(ApiConfig.ApiOrigin.DETAIL).getDetailUser(mName)
        client.enqueue(object : Callback<ResponseUserDetail> {
            override fun onResponse(
                call: Call<ResponseUserDetail>,
                response: Response<ResponseUserDetail>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _name.value = response.body()?.name
                    _urlPhoto.value = response.body()?.avatarUrl
                    _follower.value = response.body()?.followers
                    _following.value = response.body()?.following
                    _company.value = response.body()?.location
                    _repo.value = response.body()?.publicRepos
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseUserDetail>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(mContext, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}