//package com.alexz.messenger.app.data.repo
//
////import com.alexz.messenger.app.data.LocalDatabase
//import com.alexz.messenger.app.data.entities.IEntityCollection
//import com.alexz.messenger.app.data.entities.imp.Channel
//import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
//import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
//import io.reactivex.Completable
//import io.reactivex.Observable
//import io.reactivex.Single
//
//class ChannelsRepository(
//        private val provider: ChannelsProvider = FirestoreChannelsProvider(),
//        private val linker: LinkProvider = FirestoreChannelsProvider(),
//       // private val channelsDao: ChannelsDao = LocalDatabase.INSTANCE.channelDao()
//) : ChannelsProvider by provider,LinkProvider by linker {
//
//    override fun get(channelId: String,collectionID: String?): Observable<Channel> =
//            provider.get(channelId)
//        //    channelsDao.get(channelId).toObservable().concatWith(provider.get(channelId))
//
//    override fun getAll(collection:IEntityCollection, limit:Int): Observable<List<Channel>> =
//          //  Observable.concatArray(
//             //       channelsDao.getAll(limit).toObservable(),
//                    provider.getAll(collection,limit)//)
//
//    override fun delete(channel: Channel): Completable =
//            provider.delete(channel)
//           // provider.delete(channel).andThen { channelsDao.delete(channel.id) }
//
//
//    override fun join(channelId: String): Single<Channel> = //Single.create {
//        provider.join(channelId)
////                .subscribe(
////                        { c ->
////                            channelsDao.add(c)
////                            it.onSuccess(c)
////                        },
////                        { t -> it.tryOnError(t) }
////                )
////    }
//}