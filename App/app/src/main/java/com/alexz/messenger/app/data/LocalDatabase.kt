//package com.alexz.messenger.app.data
//
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import com.alexz.messenger.app.data.ChatApplication.Companion.AppContext
//import com.alexz.messenger.app.data.entities.converters.CATypeConverter
//import com.alexz.messenger.app.data.entities.converters.MCTypeConverter
//import com.alexz.messenger.app.data.entities.dao.*
//import com.alexz.messenger.app.data.entities.imp.*
//
//@Database(entities = [
//    User::class,
//    Chat::class,
//    Message::class,
//    Channel::class,
//    Post::class,
//    MediaMessage::class,
//    VoiceMessage::class
//],version = 1)
//@TypeConverters(CATypeConverter::class,MCTypeConverter::class)
//abstract class LocalDatabase : RoomDatabase() {
//    abstract fun channelDao(): ChannelsDao
//    abstract fun postsDao(): PostsDao
//    abstract fun chatsDao(): ChatsDao
//    abstract fun usersDao(): UsersDao
//    abstract fun messagesDao(): MessagesDao
//
//    companion object  {
//        val INSTANCE : LocalDatabase by lazy{
//            Room.databaseBuilder(AppContext, LocalDatabase::class.java, "local_database")
//                    .build()
//        }
//    }
//}