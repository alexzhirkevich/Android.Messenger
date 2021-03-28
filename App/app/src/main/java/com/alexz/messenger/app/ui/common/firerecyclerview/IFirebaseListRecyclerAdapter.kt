package com.alexz.messenger.app.ui.common.firerecyclerview

import com.alexz.messenger.app.data.model.interfaces.IBaseModel
import com.google.firebase.database.Query

/**
 * Recycler adapter interface for Firebase Realtime Database objects accessed as list
 *
 * @param Model object class implements [IBaseModel]
 * @param VH ViewHolder implements [IFirebaseViewHolder]
 *
 * @see IBaseModel
 * @see IFirebaseViewHolder
 */
interface IFirebaseListRecyclerAdapter<Model : IBaseModel, VH : IFirebaseViewHolder<Model>> {
    /**
     * @return Firebase [Query] object for Models list
     */
    fun onCreateModelsQuery(): Query
}