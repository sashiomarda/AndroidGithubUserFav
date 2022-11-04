package com.example.githubuser.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.githubuser.database.Fav
import com.example.githubuser.database.FavDao
import com.example.githubuser.database.FavRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavRepository(application: Application) {
    private val mFavDao: FavDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavRoomDatabase.getDatabase(application)
        mFavDao = db.favDao()
    }

    fun getAllFav(): LiveData<List<Fav>> = mFavDao.getAllFav()
    fun checkIfExist(mUserName: String): LiveData<List<Fav>> = mFavDao.checkIfExist(mUserName)
    fun insert(fav: Fav) {
        executorService.execute { mFavDao.insert(fav) }
    }

    fun delete(fav: Fav) {
        executorService.execute { mFavDao.delete(fav) }
    }

}