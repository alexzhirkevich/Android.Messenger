//package com.community.messenger.app.data.repo
//
//import com.community.messenger.app.data.LocalDatabase
//import com.community.messenger.common.entities.dao.ChannelsDao
//import com.community.messenger.common.entities.dao.ChatsDao
//import com.community.messenger.common.entities.dao.UsersDao
//import com.community.messenger.common.entities.imp.User
//import com.community.messenger.core.providers.imp.FirestoreChannelsProvider
//import com.community.messenger.core.providers.imp.FirestoreChatsProvider
//import com.community.messenger.core.providers.imp.FirestoreUserListProvider
//import com.community.messenger.core.providers.interfaces.ChannelsProvider
//import com.community.messenger.core.providers.interfaces.ChatsProvider
//import com.community.messenger.core.providers.interfaces.UserListProvider
//import io.reactivex.Completable
//import io.reactivex.Observable
//
//class UserListRepository(
//        private val userListProvider : UserListProvider = FirestoreUserListProvider(),
//        private val channelsProvider : ChannelsProvider = FirestoreChannelsProvider(),
//        private val chatsProvider: ChatsProvider = FirestoreChatsProvider(),
//        private val usersDao : UsersDao = LocalDatabase.INSTANCE.usersDao(),
//        private val chatsDao: ChatsDao = LocalDatabase.INSTANCE.chatsDao(),
//        private val channelsDao: ChannelsDao = LocalDatabase.INSTANCE.channelDao()) : UserListProvider {
//
//    override fun getUser(userID: String): Observable<User> =
//            usersDao.get(userID).toObservable().concatWith(userListProvider.get(id = userID))
//
//    override fun onChannelJoin(channelId: String): Completable =
//            userListProvider.onChannelJoin(channelId).andThen {
//                channelsProvider.get(channelId)
//                        .subscribe(
//                                { c -> channelsDao.add(c) },
//                                {t->it.onError(t)}
//                        )
//            }
//
//    override fun onChannelLeft(channelId: String): Completable =
//            userListProvider.onChannelLeft(channelId).andThen {
//                channelsDao.delete(channelId)
//            }
//
//    override fun onChatJoin(chatId: String): Completable =
//            userListProvider.onChatJoin(chatId).andThen {
//                chatsProvider.get(chatId).subscribe (
//                        { c -> chatsDao.add(c) },
//                        {t->it.onError(t)})
//            }
//
//    override fun onChatLeft(chatId: String): Completable =
//            userListProvider.onChatLeft(chatId).andThen {
//                chatsDao.delete(chatId)
//            }
//
//    override fun get(id: String, collectionID: String?): Observable<User> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAll(collectionID: String, limit: Int): Observable<List<User>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun create(entity: User): Completable {
//        TODO("Not yet implemented")
//    }
//
//    override fun delete(entity: User): Completable {
//        TODO("Not yet implemented")
//    }
//
//    override fun remove(id: String, collectionID: String?): Completable {
//        TODO("Not yet implemented")
//    }
//}