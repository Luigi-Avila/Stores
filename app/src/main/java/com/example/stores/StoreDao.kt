package com.example.stores

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StoreDao {

    @Query("SELECT * FROM StoreEntity")
    fun getAllStores(): MutableList<StoreEntity>

    @Insert
    fun insertStore(store: StoreEntity) : Long

    @Update
    fun updateStore(store: StoreEntity)

    @Delete
    fun deleteStore(store: StoreEntity)
}
