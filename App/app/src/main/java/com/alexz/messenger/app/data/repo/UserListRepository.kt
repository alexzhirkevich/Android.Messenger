//package com.alexz.messenger.app.data.repo
//
//import com.alexz.messenger.app.data.LocalDatabase
//import com.alexz.messenger.app.data.entities.dao.ChannelsDao
//import com.alexz.messenger.app.data.entities.dao.ChatsDao
//import com.alexz.messenger.app.data.entities.dao.UsersDao
//import com.alexz.messenger.app.data.entities.imp.User
//import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
//import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
//import com.alexz.messenger.app.data.providers.imp.FirestoreUserListProvider
//import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
//import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
//import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
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