//package com.alexz.messenger.app.data.repo
//
//import com.alexz.messenger.app.data.LocalDatabase
//import com.alexz.messenger.app.data.entities.dao.ChatsDao
//import com.alexz.messenger.app.data.entities.imp.Chat
//import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
//import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
//import io.reactivex.Completable
//import io.reactivex.Observable
//import io.reactivex.Single
//
//class ChatsRepository(
//        private val chatsProvider: ChatsProvider = FirestoreChatsProvider(),
//        private val chatsDao: ChatsDao = LocalDatabase.INSTANCE.chatsDao()
//) : ChatsProvider by chatsProvider{
//    override fun getAll(collectionID: String, limit:Int): Observable<List<Chat>> = Observable.concatArray(
//            chatsDao.getAll(limit).toObservable(),
//            chatsProvider.getAll(collectionID,limit)
//    )
//
//    override fun get(id: String, collectionID: String?): Observable<Chat> =
//            chatsDao.get(id).toObservable().concatWith(chatsProvider.get(id))
//
//    override fun create(entity: Chat): Completable =
//            chatsProvider.create(entity).andThen(chatsDao.add(entity))
//
//    override fun delete(entity: Chat): Completable =
//            chatsProvider.delete(entity).andThen(chatsDao.delete(entity.id))
//
//    override fun join(chatId: String): Single<Chat> =
//            chatsProvider.join(chatId)
//}