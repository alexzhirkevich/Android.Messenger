package com.alexz.messenger.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexz.messenger.app.data.entities.dao.*
import com.alexz.messenger.app.data.entities.imp.*

@Database(entities = [
    User::class,
    Chat::class,
    Message::class,
    Channel::class,
    Post::class
],version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelsDao
    abstract fun postsDao(): PostsDao
    abstract fun chatsDao(): ChatsDao
    abstract fun usersDao(): UsersDao
    abstract fun messagesDao(): MessagesDao

    companion object {
        lateinit var INSTANCE : LocalDatabase
    }
}