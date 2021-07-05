package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Group
import com.alexz.messenger.app.data.entities.imp.MediaMessage
import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.entities.imp.VoiceMessage
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.entities.interfaces.IMessage
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.util.FirebaseUtil.MEDIA_CONTENT
import com.alexz.messenger.app.util.FirebaseUtil.MESSAGES
import com.alexz.messenger.app.util.FirebaseUtil.VOICE_URI
import com.alexz.messenger.app.util.FirebaseUtil.chatsCollection
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObjectNonNull
import com.alexz.messenger.app.util.toObservable
import dagger.Component
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesProviderImp @Inject constructor() : MessagesProvider {

    override fun get(id: String, collectionID: String): Observable<IMessage> =
        chatsCollection.document(collectionID).collection(MESSAGES).document(id).toObservable { doc ->
            when  {
                doc.contains(MEDIA_CONTENT) -> doc.toObjectNonNull(MediaMessage::class.java)
                doc.contains(VOICE_URI) -> doc.toObjectNonNull(VoiceMessage::class.java)
                else -> doc.toObjectNonNull(Message::class.java)
            }
        }.map { it as IMessage }


    override fun create(entity: IMessage): Completable  {
        if (entity.chatId.isEmpty()) {
            return Completable.error(Exception("Chat for message not found"))
        }
        val doc = chatsCollection.document(entity.chatId).collection(MESSAGES).document()
        entity.id = doc.id
        return doc.set(entity).toCompletable()
    }

    override fun delete(entity: IMessage): Completable = remove(entity.id, Group(id =entity.chatId))

    override fun remove(id: String, collection: IChat): Completable =
            chatsCollection.document(collection.id).collection(MESSAGES).document(id).delete().toCompletable()

    override fun getAll(collection: IChat, limit: Int): Observable<List<IMessage>> = Observable.just(emptyList())

    override fun last(chatId: String): Observable<IMessage> = Observable.create {
        chatsCollection.document(chatId).collection(MESSAGES).limitToLast(1)
                .toObservable { doc ->
                    when {
                        doc.contains(MEDIA_CONTENT) -> doc.toObjectNonNull(MediaMessage::class.java)
                        doc.contains(VOICE_URI) -> doc.toObjectNonNull(VoiceMessage::class.java)
                        else -> doc.toObjectNonNull(Message::class.java)
                    }
                }.map { list -> list.first() }
    }
}

@Singleton
@Component
interface MessagesProviderComponent{
    fun getMessagesProvider() : MessagesProviderImp
}