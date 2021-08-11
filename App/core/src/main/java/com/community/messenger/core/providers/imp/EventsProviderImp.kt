package com.community.messenger.core.providers.imp

import com.community.messenger.common.entities.imp.Event
import com.community.messenger.common.entities.interfaces.IEvent
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.EventsProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_EVENTS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_TIME
import com.community.messenger.core.providers.interfaces.UsersProvider
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsProviderImp @Inject constructor(
        private val firebaseProvider: FirebaseProvider,
        private val usersProvider: UsersProvider
) : EventsProvider {

    override fun get(id: String): Observable<IEvent> =
            firebaseProvider.eventCollection.document(id).toObservable(Event::class.java)
                .map { it }

    override fun create(entity: IEvent): Completable {
        val doc = firebaseProvider.eventCollection.document()
        entity.id = doc.id
        entity.creatorId = usersProvider.currentUserId
        return doc.set(entity).toCompletable()
    }

    override fun delete(entity: IEvent): Completable {
        return if (entity.creatorId == usersProvider.currentUserId)
            firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                    .collection(COLLECTION_EVENTS).document(entity.id).delete().toCompletable()
        else remove(entity.id)
    }

    override fun getAll(limit: Int): Observable<Collection<IEvent>> {
        var query = firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                .collection(COLLECTION_EVENTS).orderBy(FIELD_TIME)
        if (limit > -1)
            query = query.limitToLast(limit.toLong())

        return query.toObservable(Event::class.java).map { it }
    }

    override fun remove(id: String): Completable =
             firebaseProvider.usersCollection.document(usersProvider.currentUserId)
                     .collection(COLLECTION_EVENTS).document(id).delete().toCompletable()
}

