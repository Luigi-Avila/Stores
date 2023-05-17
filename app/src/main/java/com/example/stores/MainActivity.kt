package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupRecyclerview()

        mBinding.fab.setOnClickListener { launchEditFragment() }

        /*
        mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())

            Thread {
                StoreApplication.database.storeDao().insertStore(store)
            }.start()

            mAdapter.add(store)
        }
         */
    }

    private fun launchEditFragment() {
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.commit()
        mBinding.fab.hide()
    }

    private fun setupRecyclerview() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)

        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores() {
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread {
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()
        mAdapter.setStores(queue.take())
    }


    override fun onclick(storeEntity: StoreEntity) {
        super.onclick(storeEntity)
    }

    override fun onFavorite(storeEntity: StoreEntity) {
        super.onFavorite(storeEntity)
        val queue = LinkedBlockingQueue<StoreEntity>()
        storeEntity.isFavorite = !storeEntity.isFavorite
        Thread {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        mAdapter.updateStore(queue.take())
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        super.onDeleteStore(storeEntity)
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        mAdapter.deleteStore(queue.take())
    }
}