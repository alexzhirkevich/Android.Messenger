package com.alexz.messenger.app.ui.activities

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.dao.AppStorageDao
import com.alexz.messenger.app.data.providers.imp.FirebaseStorageProvider
import com.alexz.messenger.app.data.providers.interfaces.StorageProvider
import com.alexz.messenger.app.data.repo.StorageRepository

class MediaViewModel(
        private val storageProvider : StorageProvider = StorageRepository(FirebaseStorageProvider(), AppStorageDao())
) : ViewModel(), StorageProvider by storageProvider{
}