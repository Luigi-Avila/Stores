package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        } else {
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)

        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }

        mBinding.etName.addTextChangedListener { validateFields(mBinding.tilName) }
        mBinding.etPhone.addTextChangedListener { validateFields(mBinding.tilPhone) }
        mBinding.etPhotoUrl.addTextChangedListener { validateFields(mBinding.tilPhotoUrl) }

    }

    private fun getStore(id: Long) {
        val queue = LinkedBlockingQueue<StoreEntity?>()
        Thread {
            mStoreEntity = StoreApplication.database.storeDao().getStoreById(id)
            queue.add(mStoreEntity)
        }.start()
        queue.take()?.let {
            // set values of store
            setUiStore(it)
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding) {
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
            /*
            Glide.with(requireActivity())
                .load(storeEntity.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imgPhoto)
             */

        }
    }

    // extension function editable for edit text
    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }

            R.id.action_save -> {
                if (mStoreEntity != null && validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)) {
                    /*val store = StoreEntity(
                    name = mBinding.etName.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim(),
                    website = mBinding.etWebsite.text.toString().trim(),
                    photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                )*/
                    with(mStoreEntity!!) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    val queue = LinkedBlockingQueue<StoreEntity>()
                    Thread {
                        if (mIsEditMode) StoreApplication.database.storeDao()
                            .updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id =
                            StoreApplication.database.storeDao().insertStore(mStoreEntity!!)
                        queue.add(mStoreEntity)
                    }.start()

                    with(queue.take()) {
                        hideKeyboard()

                        if (mIsEditMode) {
                            mActivity?.updateStore(this)
                            Toast.makeText(
                                mActivity,
                                R.string.edit_store_message_update_success,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mActivity?.addStore(this)
                            // This option doesn't hide the fab
                            Toast.makeText(
                                mActivity,
                                R.string.edit_store_message_save_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            /*Snackbar.make(
                                mBinding.root,
                                getString(R.string.edit_store_message_save_success),
                                Snackbar.LENGTH_SHORT
                            ).show()
                             */
                            mActivity?.onBackPressedDispatcher?.onBackPressed()
                        }

                    }

                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var validate = true

        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
                validate = false
            } else textField.error = null
        }

        if (!validate) Toast.makeText(
            mActivity,
            R.string.edit_store_message_valid,
            Toast.LENGTH_SHORT
        ).show()

        return validate
    }

    /*  private fun validateFields(): Boolean {
          var validate = true

          if (mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
              mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
              mBinding.tilPhotoUrl.requestFocus()
              validate = false
          }

          if (mBinding.etPhone.text.toString().trim().isEmpty()){
              mBinding.tilPhone.error = getString(R.string.helper_required)
              mBinding.tilPhone.requestFocus()
              validate = false
          }

          if (mBinding.etName.text.toString().trim().isEmpty()){
              mBinding.tilName.error = getString(R.string.helper_required)
              mBinding.tilName.requestFocus()
              validate = false
          }


          return validate
      }

     */

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        setHasOptionsMenu(false)
        mActivity?.hideFab(isVisible = true)
        super.onDestroy()
    }
}