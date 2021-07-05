package com.alexz.firerecadapter

import androidx.annotation.CallSuper
import com.alexz.firerecadapter.viewholder.BaseViewHolder

abstract class FirebaseRecyclerAdapter<Entity : IEntity, VH : BaseViewHolder<Entity>>(val modelClass: Class<Entity>)
    : BaseRecyclerAdapter<Entity,VH>() {

    private val databaseModels = mutableSetOf<String>()

    fun sync() = Thread {
        entities.forEach {
            if (it.id !in databaseModels)
                remove(it.id)
        }
    }.start()

    @CallSuper
    open fun onStartLoading() {
        if (!isLoading) {
            isLoading = true
            uiHandler.post {
                loadingCallback?.onStartLoading()
            }
        }
    }

    @CallSuper
    open fun onEndLoading() {
        if (isLoading) {
            isLoading = false
            uiHandler.post {
                loadingCallback?.onEndLoading()
            }
        }
    }

    @CallSuper
    override fun add(entity: Entity, forceCallback: Boolean, byUser: Boolean): Int {
        if (!byUser) {
            databaseModels.add(entity.id)
        }
        return super.add(entity, forceCallback, byUser)
    }

//    @CallSuper
//    override fun insert(idx: Int, model: Entity, forceCallback: Boolean, byUser: Boolean): Int {
//        if (!byUser) {
//            databaseModels.add(model.id)
//        }
//        return super.insert(idx, model, forceCallback, byUser)
//    }

    @CallSuper
    override fun remove(id: String, byUser: Boolean): Boolean {
        if (!byUser) {
            databaseModels.remove(id)
        }
        return super.remove(id, byUser)
    }
}