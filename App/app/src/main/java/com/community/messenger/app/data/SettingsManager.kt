package com.community.messenger.app.data

import com.community.messenger.core.providers.base.Synchronizable
import com.community.messenger.core.providers.interfaces.SettingsProvider

interface SettingsManager : SettingsProvider,Synchronizable{

    val messenger : Messenger
    val activity : Activity

    interface Messenger{

        var read : Int

        companion object{
            const val READ_ALL = 0
            const val READ_GROUPS = 1
            const val READ_DIALOGS = 1
            const val READ_NEVER = -1
        }
    }

    interface Activity{

        var online : Int

        companion object Values{
            const val ONLINE_DEFAULT = 0
            const val ONLINE_ALWAYS = 1
            const val ONLINE_NEVER =-1
        }
    }
}