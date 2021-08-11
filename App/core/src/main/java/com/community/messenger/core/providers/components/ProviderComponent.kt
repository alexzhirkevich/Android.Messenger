package com.community.messenger.core.providers.components

import com.community.messenger.core.providers.base.Provider

interface ProviderComponent<ProviderImp : Provider> {
    fun getProvider() : ProviderImp
}