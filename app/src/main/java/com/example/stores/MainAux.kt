package com.example.stores

interface MainAux {
    fun hideFab(isVisible: Boolean = false) {}

    fun addStore(store: StoreEntity){}
}