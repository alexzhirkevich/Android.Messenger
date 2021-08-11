package com.community.botapi

import com.community.botapi.database.Config.BOTS
import com.community.botapi.database.Config.CHATS
import com.community.botapi.database.Config.MESSAGES
import com.community.botapi.database.imp.Message
import com.community.botapi.database.imp.User
import java.lang.Runnable
import com.google.firebase.FirebaseOptions
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.ClassCastException
import java.lang.Exception
import java.lang.reflect.Method
import java.util.*


@Target(AnnotationTarget.FUNCTION)
annotation class MessageHandler(vararg val types : Type = [Type.ALL] ){
    enum class Type{ TEXT, VOICE, CONTENT, ALL}
}

@Target(AnnotationTarget.FUNCTION)
annotation class ActionHandler(vararg val types : Type = [Type.ALL] ){
    enum class Type { START, STOP, ALL }
}

abstract class CommunityBot : Runnable {

    abstract val key: String

    fun sendMessage(message: Message) {
        FirebaseDatabase.getInstance().reference.child(BOTS)
    }

    final override fun run() {
        if (!isRunning) {
            isRunning = true
            queueDisposable = startListening()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        processMessage(it)
                    },
                    { System.err.println(it) }
                )

            val scanner = Scanner(System.`in`)
            var nextLine: String? = ""
            while (nextLine != null && nextLine != STOP_WORD) {
                println(MSG_EXIT)
                nextLine = scanner.nextLine()
                continue
            }
            queueDisposable?.dispose()
        }
    }





    private companion object {
        private const val DB_PATH = "https://messenger-302121-default-rtdb.europe-west1.firebasedatabase.app/"
        private const val STOP_WORD = "exit"
        private const val MSG_EXIT = "Press Ctrl+C or type '${STOP_WORD}' to stop"
    }

    init {
        try {
            initialize(key)
            findMethods()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var isRunning = false
    private var queueDisposable: Disposable? = null

    private lateinit var textMessageMethods: List<Method>
    private lateinit var voiceMessageMethods: List<Method>
    private lateinit var contentMessageMethods: List<Method>
    private lateinit var startMethods: List<Method>
    private lateinit var stopMethods: List<Method>

    private fun initialize(botApiKey: String) {
        val serviceAccount = javaClass.classLoader.getResourceAsStream("credentials.json")
        val auth: MutableMap<String, Any> = HashMap()
        auth["uid"] = botApiKey
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(DB_PATH)
            .setDatabaseAuthVariableOverride(auth)
            .build()
        FirebaseApp.initializeApp(options)
    }

    private fun findMethods() {
        textMessageMethods = javaClass.methods.filter {
            it.annotations.any { a -> a is MessageHandler && a.types.contains(MessageHandler.Type.TEXT) } &&
                    it.parameterCount == 1 && it.parameterTypes.contentEquals(arrayOf(Message::class.java))
        }
        startMethods = javaClass.methods.filter {
            it.annotations.any { a -> a is ActionHandler && a.types.contains(ActionHandler.Type.START) } &&
                    it.parameterCount == 2 &&
                    (it.parameterTypes.contentEquals(arrayOf(User::class.java, ActionHandler.Type::class.java)) ||
                            (it.parameterTypes.contentEquals(arrayOf(User::class.java))))

        }
    }


    private fun startListening(): Observable<Message> = Observable.create {
        FirebaseDatabase.getInstance().reference.child(key).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(ds: DataSnapshot?, p1: String?) {
                if (ds != null)
                    getMessage(ds.key, ds.value as String).subscribe(
                        { msg ->
                            it.onNext(msg)
                            removeMessageFromQueue(msg.id)
                        },
                        { t ->
                            it.tryOnError(t)
                        }
                    )
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onCancelled(p0: DatabaseError?) {
            }

        })
    }

    private fun processMessage(message: Message) {
        textMessageMethods.forEach { it(message) }
    }

    private fun getMessage(chatId: String, msgId: String): Single<out Message> = Single.create {
        FirestoreClient.getFirestore().collection(CHATS).document(chatId).collection(MESSAGES).document(msgId)
            .addSnapshotListener { documentSnapshot, firestoreException ->
                if (firestoreException != null) {
                    it.tryOnError(firestoreException)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    val msg = documentSnapshot.toObject(Message::class.java)
                    if (msg != null) {
                        it.onSuccess(msg)
                    } else
                        it.tryOnError(ClassCastException("Cannot create msg from data: ${documentSnapshot.data}"))
                }
            }
    }

    private fun removeMessageFromQueue(id: String) {
        FirebaseDatabase.getInstance().reference.child(BOTS).child(key).child(id).removeValueAsync()
    }

}