package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.IEntityCollection
import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.util.FirebaseUtil.MESSAGES
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObjectNonNull
import io.reactivex.Completable
import io.reactivex.Observable

class FirestoreMessagesProvider : MessagesProvider {

    override fun get(id: String, collectionID: String?): Observable<Message> = Observable.create {
        if (collectionID != null) {
            chatsCollection.document(collectionID).collection(MESSAGES).document(id).get()
                    .addOnSuccessListener { ds ->
                        try {
                            it.onNext(ds.toObjectNonNull(Message::class.java))
                        } catch (t: Throwable) {
                            it.tryOnError(t)
                        }
                    }.addOnFailureListener { t -> it.tryOnError(t) }
        } else
            it.tryOnError(Exception("2nd parameter <collectionId> is required in " +
                    "${FirestoreMessagesProvider::class.simpleName}.get"))
    }

    override fun getAll(collection: IEntityCollection, limit: Int): Observable<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun create(entity: Message): Completable = Completable.create {
        if (entity.chatId.isEmpty()) {
            it.tryOnError(Exception("Chat for message not found"))
        }
        val doc = chatsCollection.document(entity.chatId)
        doc.get()
                .addOnFailureListener { ex ->
                    it.tryOnError(ex)
                }
                .addOnSuccessListener { _ ->
                    val msgDoc = doc.collection(MESSAGES).document()
                    entity.id = msgDoc.id
                    msgDoc.set(entity)
                            .addOnSuccessListener { _ ->
                                it.onComplete()
                            }
                            .addOnFailureListener { t ->
                                it.tryOnError(t)
                            }
                }
    }

    override fun delete(entity: Message): Completable = remove(entity.id, Chat(id =entity.chatId))

    override fun remove(id: String, collection: IEntityCollection?): Completable  {
        return if (collection != null) {
            if (Message::class.java in collection) {
                chatsCollection.document(collection.id).collection(MESSAGES).document(id).delete().toCompletable()
            } else
                Completable.error(IllegalArgumentException("Cannot get messages: invalid collection"))
        } else Completable.error(Exception("2nd parameter <collectionId> is required in " +
                "${FirestoreMessagesProvider::class.simpleName}.get"))
    }

    override fun last(chatId: String): Observable<Message> = Observable.create {
        chatsCollection.document(chatId).collection(MESSAGES).limitToLast(1)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        it.tryOnError(error)
                        return@addSnapshotListener
                    }
                    if (value == null || value.size() == 0) {
                        it.tryOnError(Exception("Failed to get last message"))
                    }
                    value?.documents?.get(0)?.toObject(Message::class.java)?.let { msg ->
                        it.onNext(msg)
                    }
                }
    }

}