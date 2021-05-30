//package com.alexz.messenger.app.data.entities
//
//import com.alexz.firerecadapter.IEntity
//import com.alexz.firerecadapter.Listenable
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.firestore.DocumentReference
//import com.google.firebase.firestore.DocumentSnapshot
//import com.google.firebase.firestore.ListenerRegistration
//import io.reactivex.Observable
//import io.reactivex.Observer
//
//class ObservableFirestoreEntity<Entity : IEntity>(
//        private var clazz: Class<Entity>,
//        private var reference: DocumentReference,
//        private var parser : ((DocumentSnapshot) -> Entity?) = {it.toObject(clazz)})
//    : Observable<Entity>(), ObservableEntity<Entity>,Listenable {
//
//    private var reg : ListenerRegistration? = null
//
//    private val observer: Observable<Entity> by lazy {
//        create<Entity> {
//            req = reference.addSnapshotListener { snap, error ->
//                if (error != null) {
//                    it.tryOnError(error)
//                    return@addSnapshotListener
//                }
//                if (snap == null || !snap.exists()) {
//                    it.tryOnError(Exception("Entity not found"))
//                    return@addSnapshotListener
//                }
//                parser(snap)?.let { value ->
//                    it.onNext(value)
//                }
//            }
//        }
//    }
//
//    override fun subscribeActual(obs: Observer<in Entity>?) {
//        observer.subscribe(obs)
//    }
//
//    var onChanged  = HashSet<((Entity) -> Unit)>()
//    private var onError = HashSet<((DatabaseError) -> Unit)>()
//
//    var isListening : Boolean = false
//    var state: Entity? = null
//        private set
//
//    fun forceUpdate(){
//        stopListening()
//        startListening()
//    }
//
//    fun addOnChangedListener(listener : (Entity) -> Unit) = onChanged.add(listener)
//
//    fun removeOnChangeListener(listener : (Entity) -> Unit) = onChanged.remove(listener)
//
//    fun addOnErrorListener(listener: (DatabaseError) -> Unit) = onError.add(listener)
//
//    fun removeOnErrorListener(listener: (DatabaseError) -> Unit)= onError.remove(listener)
//
//    override fun startListening() {
//        if (!isListening){
//            reg = reference?.addSnapshotListener { value, error ->
//                if (error != null || value ==  null){
//                    return@addSnapshotListener
//                }
//                val entity = parser(value)
//                if (entity != null){
//                    state = entity
//                    onChanged.forEach { it(entity) }
//                }
//            }
//            isListening = true
//        }
//    }
//
//    override fun stopListening() {
//        if (isListening) {
//            isListening = false
//            reg?.remove()
//            reg = null
//        }
//    }
//}