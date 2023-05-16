package com.example.stores

interface OnClickListener {

    fun onclick(storeEntity: StoreEntity){}

    fun onFavorite(storeEntity: StoreEntity){}

    fun onDeleteStore(storeEntity: StoreEntity){}
}