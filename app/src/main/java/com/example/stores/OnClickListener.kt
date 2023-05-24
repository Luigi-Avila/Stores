package com.example.stores

interface OnClickListener {

    fun onclick(storeId: Long){}

    fun onFavorite(storeEntity: StoreEntity){}

    fun onDeleteStore(storeEntity: StoreEntity){}
}