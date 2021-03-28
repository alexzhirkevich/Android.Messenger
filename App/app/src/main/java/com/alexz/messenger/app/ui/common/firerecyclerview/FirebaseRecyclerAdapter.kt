package com.alexz.messenger.app.ui.common.firerecyclerview

import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.data.model.interfaces.IBaseModel
import com.alexz.messenger.app.ui.common.ItemClickListener
import com.google.firebase.database.DataSnapshot
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Model class for [DataSnapshot] parsing
 * @param modelClass - class of the adapter Model
 * @see IBaseModel
 */
abstract class FirebaseRecyclerAdapter<Model : IBaseModel, VH : FirebaseViewHolder<Model>>
(override val modelClass: Class<Model>) : RecyclerView.Adapter<VH>(),
        IFirebaseRecyclerAdapter<Model, VH> {

    override var itemClickListener: ItemClickListener<Model>? = null
    override var adapterCallback: AdapterCallback<Model>? = null
    override var loadingCallback: LoadingCallback? = null

    protected val uiHandler = Handler(Looper.getMainLooper())
    protected var isSearching = false

    private val models: MutableList<Model> = CopyOnWriteArrayList()
    private var selected: List<Model> = models

    /**
     * Makes view clickable and sets click listener
     * Use [IFirebaseRecyclerAdapter.onCreateClickableViewHolder] to create ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = onCreateClickableViewHolder(parent, viewType)
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.isLongClickable = true
        holder.itemView.requestLayout()
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(it, holder.model)
        }
        holder.itemView.setOnLongClickListener {
            itemClickListener?.onLongItemClick(it, holder.model) ?: false
        }

        return holder
    }

    /**
     * [IFirebaseViewHolder.bind] used to bind a ViewHolder
     */
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(selected[position])

    /**
     * @return current selected items count
     *
     * @see IFirebaseRecyclerAdapter.select
     * @see IFirebaseRecyclerAdapter.selectAll
     */
    override fun getItemCount(): Int = selected.size

    /**
     * Clears all elements.
     *
     * **[!] Elements can return if adapter is listening**
     * **To prevent this, use [Listenable.stopListening] before clearing**
     * @see Listenable
     */
    fun clear() {
        removeAll()
        notifyDataSetChanged()
    }

    /**
     * @see IFirebaseRecyclerAdapter.select
     */
    override fun select(selectionKey: String?): Int {
        if (selectionKey == null) {
            selectAll()
        } else {
            isSearching = true
            selected = models.filter { onSelect(selectionKey, it) }
            notifyDataSetChanged()
        }
        return selected.size
    }

    /**
     * @see IFirebaseRecyclerAdapter.selectAll
     */
    override fun selectAll() {
        if (selected !== models) {
            selected = models
            notifyDataSetChanged()
            isSearching = false
        }
    }

    protected fun realItemCount(): Int = models.size

    override fun add(model: Model): Int = synchronized(models) {
        val idx = models.indexOfFirst { it.id == model.id }
        if (idx != -1) {
            models[idx] = model
            idx
        } else {
            models.add(model)
            models.size
        }
    }

    override fun insert(idx: Int, model: Model) : Int = synchronized(models) {
        val findIdx = models.indexOfFirst { it.id == model.id }
        if (findIdx != -1) {
            models[idx] = model
            findIdx
        } else {
            models.add(idx,model)
            idx
        }
    }

    protected fun remove(id: String): Pair<Model, Int>? = synchronized(models) {
        val idx = models.indexOfFirst { it.id == id }
        if (idx != -1) Pair(models.removeAt(idx), idx) else null
    }

    protected fun removeAll()  = synchronized(models) {
        models.clear()
    }

    companion object {
        private val TAG = FirebaseRecyclerAdapter::class.java.simpleName
    }
}