package com.alexz.firerecadapter

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.realtimedb.RealtimeDatabaseListRecyclerAdapter
import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
import com.google.firebase.database.DataSnapshot
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Base recycler adapter for Firebase Realtime Database objects
 * Provides entities managing, filtering
 *
 * @param modelClass default model class, used to [parse] a [DataSnapshot]
 * @see IEntity
 * @see IBaseRecyclerAdapter
 */
abstract class BaseRecyclerAdapter<Entity : IEntity, VH : FirebaseViewHolder<Entity>>
(override val modelClass: Class<Entity>) : RecyclerView.Adapter<VH>(),
        IBaseRecyclerAdapter<Entity, VH> {

    override var itemClickListener: ItemClickListener<Entity>? = null
    override var adapterCallback: AdapterCallback<Entity>? = null
    override var loadingCallback: LoadingCallback? = null

    var isListen  = false
        protected set
    var isLoading = false
        protected set
    var isSearching = false
        private set

    protected val uiHandler = Handler(Looper.getMainLooper())

    private val _models: MutableList<Entity> = CopyOnWriteArrayList()

    val models : List<Entity>
        get() = _models

    private var selected: List<Entity> = _models

    /**
     * Makes view clickable and sets click listener
     * Use [IBaseRecyclerAdapter.onCreateClickableViewHolder] to create ViewHolder
     */
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        onCreateClickableViewHolder(parent, viewType).apply {
            itemView.isClickable = true
            itemView.isFocusable = true
            itemView.isLongClickable = true
            itemView.requestLayout()
            itemView.setOnClickListener {
                uiHandler.post {
                    itemClickListener?.onItemClick(this)
                }
            }
            itemView.setOnLongClickListener {
                uiHandler.post {
                    itemClickListener?.onLongItemClick(this)
                }
            }
        }

    /**
     * [IFirebaseViewHolder.bind] used to bind a ViewHolder
     */
    final override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(selected[position])

    /**
     * @return current selected items count
     *
     * @see IBaseRecyclerAdapter.select
     * @see IBaseRecyclerAdapter.selectAll
     */
    final override fun getItemCount(): Int = selected.size

    /**
     * Clears all elements.
     *
     * **[!] Elements can return if adapter is listening**
     * **To prevent this, use [Listenable.stopListening] before clearing**
     * @see Listenable
     */
    fun clear() {
        removeAll()
        uiHandler.post {
            notifyDataSetChanged()
        }
    }

    /**
     * @see IBaseRecyclerAdapter.select
     */
    final override fun select(key: String?): Int {
        if (key == null) {
            selectAll()
        } else {
            isSearching = true
            selected = _models.filter { onSelect(key, it) }
            uiHandler.post {
                notifyDataSetChanged()
            }
        }
        return selected.size
    }

    /**
     * @see IBaseRecyclerAdapter.selectAll
     */
    final override fun selectAll() {
        if (selected !== _models) {
            selected = _models
            uiHandler.post {
                notifyDataSetChanged()
            }
            isSearching = false
        }
    }

    protected fun realItemCount(): Int = _models.size

    @CallSuper
    override fun add(model: Entity, forceCallback:Boolean, byUser : Boolean): Int = synchronized(_models) {
        val idx = _models.indexOfFirst { it.id == model.id }
        return if (idx != -1) {
            _models[idx] = model
            uiHandler.post {
                if (!isSearching) {
                    notifyItemChanged(idx)
                }
                adapterCallback?.onItemChanged(model)
            }
            idx
        } else {
            _models.add(model)
            uiHandler.post {
                if (!isSearching) {
                    notifyItemInserted(_models.size)
                }
            }
            if (!isLoading || forceCallback) {
                adapterCallback?.onItemAdded(model)
            }
            _models.size
        }
    }

    @CallSuper
    override fun insert(idx: Int, model: Entity,forceCallback: Boolean, byUser : Boolean) : Int = synchronized(_models) {
        val findIdx = _models.indexOfFirst { it.id == model.id }
        return if (findIdx != -1) {
            _models[idx] = model
            uiHandler.post {
                if (!isSearching) {
                    notifyItemChanged(idx)
                }
                adapterCallback?.onItemChanged(model)
            }
            findIdx
        } else {
            _models.add(idx,model)
            uiHandler.post {
                if (!isSearching) {
                    notifyItemInserted(idx)
                }
                if (!isLoading || forceCallback) {
                    adapterCallback?.onItemAdded(model)
                }
            }
            idx
        }

    }

    @CallSuper
    override fun remove(id: String, byUser : Boolean): Boolean = synchronized(_models) {
        val idx = _models.indexOfFirst { it.id == id }
        if (idx != -1) {
            val model = _models.removeAt(idx)
            uiHandler.post {
                notifyItemRemoved(idx)
                adapterCallback?.onItemRemoved(model)
            }
            true
        } else false
    }

    @CallSuper
    protected fun removeAll()  = synchronized(_models) {
        _models.clear()
    }

    companion object {
        private val TAG = RealtimeDatabaseListRecyclerAdapter::class.java.simpleName
    }
}