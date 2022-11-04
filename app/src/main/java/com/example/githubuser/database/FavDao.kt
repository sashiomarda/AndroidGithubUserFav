package com.example.githubuser.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fav: Fav)

    @Update
    fun update(fav: Fav)

    @Delete
    fun delete(fav: Fav)

    @Query("SELECT * from Fav ORDER BY id ASC")
    fun getAllFav(): LiveData<List<Fav>>

    @Query("SELECT * from Fav WHERE username = :usrnm")
    fun checkIfExist(usrnm: String): LiveData<List<Fav>>
}