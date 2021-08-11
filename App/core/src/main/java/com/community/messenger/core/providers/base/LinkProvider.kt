package com.community.messenger.core.providers.base

interface LinkProvider : Provider{

    fun createInviteLink(id : String) : String
}