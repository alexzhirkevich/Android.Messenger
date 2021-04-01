package com.alexz.messenger.app.ui.dialogwindows

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import com.alexz.messenger.app.data.model.imp.Chat
import com.alexz.messenger.app.data.repo.DialogsRepository.createChat
import com.alexz.messenger.app.data.repo.DialogsRepository.findChat
import com.alexz.messenger.app.ui.activities.ChatActivity
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.storage.StorageReference
import com.messenger.app.R

class AddChatDialog(private val fragment: Fragment) : AlertDialog(fragment.context), View.OnClickListener, DialogResult {

    private var newChatDialog: NewChatDialog? = null

    override fun onClick(view: View) {
        if (view.id == R.id.btn_new_chat) {
            onBackPressed()
            newChatDialog = NewChatDialog(fragment)
            newChatDialog?.show()
        } else if (view.id == R.id.btn_find_chat) {
            FindChatDialog(context).show()
            onBackPressed()
        }
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        newChatDialog?.onDialogResult(requestCode, resultCode, resultIntent)
    }

    class FindChatDialog(context: Context?) : AlertDialog(context), View.OnClickListener, TextWatcher, OnEditorActionListener {
        private val editId: EditText?
        private val btnOk: Button?
        override fun onClick(view: View) {
            if (editId != null) {
                val id = editId.text.toString().trim { it <= ' ' }
                findChat(id)
                        .addOnSuccessResultListener {
                            if (it != null) {
                                ChatActivity.startActivity(context, it)
                            }
                        }
                        .addOnErrorResultListener {
                            Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
                        }
                onBackPressed()
            }
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (btnOk != null) {
                btnOk.isEnabled = charSequence.isNotEmpty()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
        override fun onEditorAction(textView: TextView, i: Int, keyEvent: KeyEvent): Boolean {
            if (i == EditorInfo.IME_ACTION_DONE && btnOk != null && btnOk.isEnabled) {
                btnOk.callOnClick()
                return true
            }
            return false
        }

        init {
            val layout = LinearLayout(getContext())
            layoutInflater.inflate(R.layout.dialog_find_chat, layout)
            setView(layout)
            editId = layout.findViewById(R.id.edit_find_chat)
            btnOk = layout.findViewById(R.id.btn_find_chat_ok)
            if (btnOk != null) {
                btnOk.setOnClickListener(this)
                btnOk.isEnabled = false
            }
            if (editId != null) {
                editId.addTextChangedListener(this)
                editId.setOnEditorActionListener(this)
            }
        }
    }

    class NewChatDialog(fragment: Fragment) : AlertDialog(fragment.context), View.OnClickListener, TextWatcher, OnEditorActionListener, DialogResult {
        private val editName: EditText?
        private val btnPhoto: Button?
        private val btnOk: Button?
        private val photoUpload: ProgressBar
        private val fragment: Fragment?
        private var imageUri: Uri? = null
        private var storageReference: StorageReference? = null
        private var deletePhoto = false
        override fun onStop() {
            super.onStop()
            if (deletePhoto) {
                storageReference?.delete()
            }
        }

        override fun onClick(view: View) {
            if (view.id == R.id.btn_create_chat) {
                var name = ""
                if (editName != null) {
                    name = editName.text.toString().trim { it <= ' ' }
                }
                if (!name.isEmpty()) {
                    deletePhoto = false
                    onBackPressed()
                    val c = Chat(imageUri?.toString()?:"",
                            name,
                            FirebaseUtil.getCurrentUser().id,
                            System.currentTimeMillis(),
                            null,
                            true)
                    createChat(c)
                    ChatActivity.startActivity(context, c)
                } else {
                    Toast.makeText(context, R.string.error_empty_input, Toast.LENGTH_LONG).show()
                }
            } else if (view.id == R.id.btn_create_photo) {
                if (!deletePhoto) {
                    if (fragment != null) {
                        val photoPickerIntent = Intent(Intent.ACTION_PICK)
                        photoPickerIntent.type = "image/*"
                        fragment.startActivityForResult(photoPickerIntent, REQ_NEW_CHAT_PHOTO)
                    }
                } else {
                    storageReference?.delete()
                    imageUri = null
                    storageReference = null
                    deletePhoto = false
                    btnPhoto?.setText(R.string.title_upload_photo)
                }
            }
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (btnOk != null) {
                btnOk.isEnabled = charSequence.isNotEmpty()
            }
        }

        override fun afterTextChanged(editable: Editable) {}
        override fun onEditorAction(textView: TextView, i: Int, keyEvent: KeyEvent): Boolean {
            if (i == EditorInfo.IME_ACTION_DONE && btnOk != null && btnOk.isEnabled) {
                btnOk.callOnClick()
                return true
            }
            return false
        }

        override fun onDialogResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
            if (requestCode == REQ_NEW_CHAT_PHOTO && resultCode == Activity.RESULT_OK) {
                btnPhoto!!.isEnabled = false
                btnOk!!.isEnabled = false
                photoUpload.visibility = View.VISIBLE
                FirebaseUtil.uploadPhoto(imageReturnedIntent?.data)
                        .addOnSuccessResultListener {
                            imageUri = it?.first
                            storageReference = it?.second
                            deletePhoto = true
                            btnPhoto.setText(R.string.title_remove_photo)
                            btnPhoto.post { btnPhoto.isEnabled = true }
                            btnOk.post { btnOk.isEnabled = true }
                            photoUpload.post { photoUpload.visibility = View.GONE }
                        }
                        .addOnErrorResultListener {
                            Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
                            btnPhoto.post { btnPhoto.isEnabled = true }
                            btnOk.post { btnOk.isEnabled = true }
                            photoUpload.post { photoUpload.visibility = View.GONE }
                        }
                        .addOnProgressListener { it?.let { photoUpload.progress = it.toInt() } }
            }
        }

        init {
            this.fragment = fragment
            val layout = LinearLayout(context)
            layoutInflater.inflate(R.layout.dialog_new_chat, layout)
            setView(layout)
            editName = layout.findViewById(R.id.edit_create_name)
            btnPhoto = layout.findViewById(R.id.btn_create_photo)
            btnOk = layout.findViewById(R.id.btn_create_chat)
            photoUpload = layout.findViewById(R.id.progress_photo_upload)
            btnOk.setOnClickListener(this)
            btnOk.isEnabled = false
            editName.addTextChangedListener(this)
            btnPhoto.setOnEditorActionListener(this)
            btnPhoto?.setOnClickListener(this)
        }
    }

    companion object {
        const val REQ_NEW_CHAT_PHOTO = 1701
    }

    init {
        val layout = LinearLayout(fragment.context)
        layoutInflater.inflate(R.layout.dialog_add_chat, layout)
        setView(layout)
        val btnNew = layout.findViewById<Button>(R.id.btn_new_chat)
        val btnFind = layout.findViewById<Button>(R.id.btn_find_chat)
        btnNew?.setOnClickListener(this)
        btnFind?.setOnClickListener(this)
    }
}