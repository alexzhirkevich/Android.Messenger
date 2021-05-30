package com.alexz.messenger.app.data.repo

interface LinkProvider {

    fun createInviteLink(id : String) : String
}