package com.community.messenger.core.providers.interfaces

import android.content.Context
import com.community.messenger.core.providers.base.Provider

interface AppInitProvider : Provider {

    fun init(context : Context)
}