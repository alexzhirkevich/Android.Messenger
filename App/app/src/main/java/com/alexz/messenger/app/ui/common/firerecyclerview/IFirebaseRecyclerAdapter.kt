package com.alexz.messenger.app.ui.common.firerecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.data.model.imp.BaseModel
import com.alexz.messenger.app.data.model.interfaces.IBaseModel
import com.alexz.messenger.app.ui.common.ItemClickListener
import com.google.firebase.database.DataSnapshot

/**
 * Recycler adapter interface for Firebase Realtime Database objects.
 * Basic interface, provides offline access.
 *
 * @param Model object class implements [IBaseModel]
 * @param VH ViewHolder implements [IFirebaseViewHolder]
 *
 * @see IBaseModel
 * @see IFirebaseViewHolder
 */
interface IFirebaseRecyclerAdapter<Model : IBaseModel, VH : IFirebaseViewHolder<Model>> {

    val modelClass : Class<Model>

    var itemClickListener: ItemClickListener<Model>?
    var adapterCallback: AdapterCallback<Model>?
    var loadingCallback: LoadingCallback?

    /**
     * Same as [RecyclerView.Adapter.onCreateViewHolder]
     *
     * Used in [FirebaseMapRecyclerAdapter.onCreateViewHolder]
     *
     * @return [FirebaseViewHolder]
     * @see IFirebaseViewHolder
     */
    fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * Decide if model must be selected by selection key
     *
     * Used in [FirebaseMapRecyclerAdapter.select]
     * @see IFirebaseRecyclerAdapter.select
     * @param selectionKey key for models selection
     * @param model extends [BaseModel]
     *
     * @return Model field for selection
     */
    fun onSelect(selectionKey: String?, model: Model): Boolean = true

    /**
     * Parse Model object from [DataSnapshot]
     *
     * @param snapshot [DataSnapshot]
     * @return parsed object or null
     */
    fun parse(snapshot: DataSnapshot): Model? = snapshot.getValue(modelClass)

    /**
     * Shows only those ViewHolders, which key field contains {@param containsString}
     *
     * Override [IFirebaseRecyclerAdapter.onSelect] to be able to select visible items.
     *
     * @see IBaseModel.id
     * @param selectionKey substring to find. Shows all items, if param is null
     *
     * @return selected count
     */
    fun select(selectionKey: String?): Int

    /**
     * Used to offline models adding. Can be changed after approving in database
     *
     * @param model model to add
     * @return inserted idx or changed idx, if model with that [IBaseModel.id] is already exists
     * */
    fun add(model : Model) : Int

    /**
     * Used to offline models inserting. Can be changed after approving in database
     *
     * @param model model to add
     * @return inserted idx or changed idx, if model with that [IBaseModel.id] is already exists
     * */
    fun insert(idx: Int, model: Model) : Int

    /**
     * Shows all ViewHolders
     */
    fun selectAll()
}