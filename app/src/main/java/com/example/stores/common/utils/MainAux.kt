package com.example.stores.common.utils

import com.example.stores.common.entities.StoreEntity

interface MainAux {
    fun hideFab(isVisible: Boolean = false) {}

    fun addStore(store: StoreEntity){}

    fun updateStore(store: StoreEntity){}
}