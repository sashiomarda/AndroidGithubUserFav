package com.example.githubuser.insert

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.githubuser.database.Fav
import com.example.githubuser.repository.FavRepository

class FavAddUpdateViewModel(application: Application) : ViewModel() {
    private val mFavRepository: FavRepository = FavRepository(application)
    fun insert(fav: Fav) {
        mFavRepository.insert(fav)
    }

    fun delete(fav: Fav) {
        mFavRepository.delete(fav)
    }
}