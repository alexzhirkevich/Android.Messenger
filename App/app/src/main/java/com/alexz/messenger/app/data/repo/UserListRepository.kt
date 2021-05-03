package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.data.LocalDatabase
import com.alexz.messenger.app.data.entities.dao.ChannelsDao
import com.alexz.messenger.app.data.entities.dao.ChatsDao
import com.alexz.messenger.app.data.entities.dao.UsersDao
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreUserListProvider
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class UserListRepository(
        private val userListProvider : UserListProvider = FirestoreUserListProvider(),
        private val channelsProvider : ChannelsProvider = FirestoreChannelsProvider(),
        private val chatsProvider: ChatsProvider = FirestoreChatsProvider(),
        private val usersDao : UsersDao = LocalDatabase.INSTANCE.usersDao(),
        private val chatsDao: ChatsDao = LocalDatabase.INSTANCE.chatsDao(),
        private val channelsDao: ChannelsDao = LocalDatabase.INSTANCE.channelDao()) : UserListProvider {

    override fun getUser(userID: String): Observable<User> =
            usersDao.get(userID).toObservable().concatWith(userListProvider.getUser(userID))

    override fun onChannelJoined(channelId: String): Completable =
            userListProvider.onChannelJoined(channelId).andThen {
                channelsProvider.getChannel(channelId).subscribe { c ->
                    channelsDao.add(c)
                }
            }

    override fun onChannelLeft(channelId: String): Completable =
            userListProvider.onChannelLeft(channelId).andThen {
                channelsDao.delete(channelId)
            }

    override fun onChatJoined(chatId: String): Completable =
            userListProvider.onChatJoined(chatId).andThen {
                chatsProvider.getChat(chatId).subscribe { c ->
                    chatsDao.add(c)
                }
            }

    override fun onChatLeft(chatId: String): Completable =
            userListProvider.onChatLeft(chatId).andThen {
                chatsDao.delete(chatId)
            }
}