package com.example.stores.mainModule.adapter

import com.example.stores.common.entities.StoreEntity

interface OnClickListener {

    fun onclick(storeId: Long){}

    fun onFavorite(storeEntity: StoreEntity){}

    fun onDeleteStore(storeEntity: StoreEntity){}
}