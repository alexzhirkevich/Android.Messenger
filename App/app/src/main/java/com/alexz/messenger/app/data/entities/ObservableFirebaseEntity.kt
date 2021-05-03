//package com.alexz.messenger.app.data.entities
//
//import com.alexz.firerecadapter.IEntity
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.Query
//import com.google.firebase.database.ValueEventListener
//import io.reactivex.rxjava3.core.Observable
//import io.reactivex.rxjava3.core.Observer
//
//class ObservableFirebaseEntity<Entity : IEntity>(
//        var clazz: Class<Entity>) :
//        Observable<Entity>(),
//        ObservableEntity<Entity>{
//
//    override var state: Entity? = null
//
//    private val observer: Observable<Entity>
//        get() = TODO("Not yet implemented")
//
//    var query : Query? = null
//        set(value) {
//            val l = isListening
//            stopListening()
//            field = value
//            if (l){
//                startListening()
//            }
//        }
//
//    var onChanged  = HashSet<((Entity) -> Unit)>()
//    private var onError = HashSet<((DatabaseError) -> Unit)>()
//    var parser : ((DataSnapshot) -> Entity?) = { it.getValue(clazz) }
//
//    var isListening : Boolean = false
//    var currentModel: Entity? = null
//        private set
//
//    private val valueEventListener = object : ValueEventListener {
//        override fun onDataChange(snapshot: DataSnapshot) {
//            if (snapshot.exists()) {
//                val model = parser(snapshot)
//                if (model != null) {
//                    currentModel = model
//                    onChanged.forEach { it(model) }
//                }
//            }
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            onError.forEach {
//                it(error)
//            }
//        }
//    }
//
//    fun forceUpdate(){
//        query?.removeEventListener(valueEventListener)
//        query?.addValueEventListener(valueEventListener)
//    }
//
//    fun addOnChangedListener(listener : (Entity) -> Unit) = onChanged.add(listener)
//
//    fun removeOnChangeListener(listener : (Entity) -> Unit) = onChanged.remove(listener)
//
//    fun addOnErrorListener(listener: (DatabaseError) -> Unit) = onError.add(listener)
//
//
//    fun removeOnErrorListener(listener: (DatabaseError) -> Unit)= onError.remove(listener)
//
//    override fun startListening() {
//        if (!isListening){
//            query?.addValueEventListener(valueEventListener)
//            isListening = true
//        }
//    }
//
//    override fun stopListening() {
//        if (isListening){
//            query?.removeEventListener(valueEventListener)
//            isListening = false
//        }
//    }
//
//    override fun subscribeActual(observer: Observer<in Entity>?) {
//        TODO("Not yet implemented")
//    }
//
//}