package com.example.raithvartha

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TipDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(tips: List<Tip>)

    @Query("SELECT * FROM tips WHERE isDailyTip = 1 LIMIT 1")
    fun getDailyTip(): LiveData<Tip>

    @Query("SELECT * FROM tips WHERE cropCategory = :category")
    fun getTipsByCategory(category: String): LiveData<List<Tip>>

    @Query("SELECT * FROM tips")
    fun getAllTips(): LiveData<List<Tip>>

    @Query("SELECT COUNT(*) FROM tips")
    suspend fun getCount(): Int
}