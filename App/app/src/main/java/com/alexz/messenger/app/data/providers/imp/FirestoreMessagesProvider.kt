package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.util.FirebaseUtil.MESSAGES
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.toObjectNonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class FirestoreMessagesProvider : MessagesProvider {

    override fun getMessage(chatId: String,msgId: String) : Observable<Message> = Observable.create {
        chatsCollection.document(chatId).collection(MESSAGES).document(msgId).get()
                .addOnSuccessListener { ds ->
                    try{
                        it.onNext(ds.toObjectNonNull(Message::class.java))
                    }catch (t : Throwable){
                        it.tryOnError(t)
                    }
                }.addOnFailureListener { t -> it.tryOnError(t) }
    }

    override fun sendMessage(message: Message): Completable = Completable.create {
        if (message.chatId.isEmpty()) {
            it.tryOnError(Exception("Chat for message not found"))
        }
        val doc = chatsCollection.document(message.chatId)
        doc.get()
                .addOnFailureListener { ex ->
                    it.tryOnError(ex)
                }
                .addOnSuccessListener { _ ->
                    val msgDoc = doc.collection(MESSAGES).document()
                    message.id = msgDoc.id
                    msgDoc.set(message)
                            .addOnSuccessListener { _ ->
                                it.onComplete()
                            }
                            .addOnFailureListener { t ->
                                it.tryOnError(t)
                            }
                }
    }

    override fun lastMessage(chatId: String): Observable<Message> = Observable.create {
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

    override fun deleteMessage(chatId: String, msgId: String): Completable  = Completable.create {
        chatsCollection.document(chatId).collection(MESSAGES).document(msgId).delete()
                .addOnSuccessListener { _ ->
                    it.onComplete()
                }
                .addOnFailureListener { ex ->
                    it.tryOnError(ex)
                }
    }
}