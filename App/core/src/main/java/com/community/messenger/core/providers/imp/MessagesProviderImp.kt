package com.community.messenger.core.providers.imp

import com.community.messenger.common.entities.imp.Group
import com.community.messenger.common.entities.imp.MediaMessage
import com.community.messenger.common.entities.imp.Message
import com.community.messenger.common.entities.imp.VoiceMessage
import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IMessage
import com.community.messenger.common.util.SnapshotNotFoundException
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_MESSAGES
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_MEDIA_CONTENT
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_VOICE_URI
import com.community.messenger.core.providers.interfaces.MessagesProvider
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesProviderImp @Inject constructor(
        private val firebaseProvider: FirebaseProvider
) : MessagesProvider {

    override fun get(id: String, collectionID: String): Observable<out IMessage> =
            firebaseProvider.chatsCollection.document(collectionID).collection(COLLECTION_MESSAGES)
                .document(id).toObservable { doc ->
            when  {
                doc.contains(FIELD_MEDIA_CONTENT) -> doc.toObject(MediaMessage::class.java)
                doc.contains(FIELD_VOICE_URI) -> doc.toObject(VoiceMessage::class.java)
                else -> doc.toObject(Message::class.java)
            } ?: throw SnapshotNotFoundException(id)
        }


    override fun create(entity: IMessage): Completable  {
        if (entity.chatId.isEmpty()) {
            return Completable.error(Exception("Chat for message not found"))
        }
        val doc = firebaseProvider.chatsCollection.document(entity.chatId).collection(COLLECTION_MESSAGES).document()
        entity.id = doc.id
        return doc.set(entity).toCompletable()
    }

    override fun delete(entity: IMessage): Completable = remove(entity.id, Group(id =entity.chatId))

    override fun remove(id: String, collection: IChat): Completable =
            firebaseProvider.chatsCollection.document(collection.id).collection(COLLECTION_MESSAGES).document(id).delete().toCompletable()

    override fun getAll(collection: IChat, limit: Int): Observable<Collection<IMessage>> = Observable.just(emptyList())

    override fun last(chatId: String): Observable<IMessage> = Observable.create {
        firebaseProvider.chatsCollection.document(chatId).collection(COLLECTION_MESSAGES).limitToLast(1)
                .toObservable { doc ->
                    when {
                        doc.contains(FIELD_MEDIA_CONTENT) -> doc.toObject(MediaMessage::class.java)
                        doc.contains(FIELD_VOICE_URI) -> doc.toObject(VoiceMessage::class.java)
                        else -> doc.toObject(Message::class.java)
                    }
                }.map { list -> list.first() }
    }
}
