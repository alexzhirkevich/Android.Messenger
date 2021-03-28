package com.alexz.messenger.app.ui.common.firerecyclerview

import com.alexz.messenger.app.data.model.interfaces.IBaseModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query

/**
 * Recycler adapter interface for Firebase Realtime Database objects accessed by key.
 * @param Model object class implements [IBaseModel]
 * @param VH ViewHolder implements [IFirebaseViewHolder]
 *
 * @see IBaseModel
 * @see IFirebaseViewHolder
 */
interface IFirebaseMapRecyclerAdapter<Model : IBaseModel, VH : IFirebaseViewHolder<Model>> {
    /**
     * @return Firebase [Query] object for Models key set ([IBaseModel.id] - key)
     */
    fun onCreateKeyQuery(): Query

    /**
     * @return Firebase [Query] object for Model by key
     * @param modelId key ([IBaseModel.id])
     */
    fun onCreateModelQuery(modelId: String): Query

    /**
     * Called when model DataSnapshot, got by key, not exists (cannot find object in DB)
     * @see DataSnapshot
     */
    fun onModelNotFound(modelId: String) {}
}