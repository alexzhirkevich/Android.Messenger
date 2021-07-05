package com.alexz.firerecadapter

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.realtimedb.RealtimeDatabaseListRecyclerAdapter
import com.alexz.firerecadapter.viewholder.BaseViewHolder
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



abstract class BaseRecyclerAdapter<Entity : IEntity, VH : BaseViewHolder<Entity>> : RecyclerView.Adapter<VH>(),
        IBaseRecyclerAdapter<Entity, VH> {

    override var itemClickListener: ItemClickListener<VH> = {}
    override var itemLongClickListener: ItemLongClickListener<VH> = { true }
    override var adapterCallback: AdapterCallback<Entity> = object : AdapterCallback<Entity>{}
    override var loadingCallback: LoadingCallback = object : LoadingCallback {}
    override var onSelectedStateChangedListener: OnSelectedStateChangedListener = {}

    var isListen = false
        protected set
    var isLoading = false
        protected set
    var isSearching = false
        private set

    val entities: List<Entity>
        get() = mEntities

    var inSelectingMode = false
        set(value) {
            field = value
            if (!value) {
                selectedItems.clear()
            } else {
                entities.forEach {
                    selectedItems[it.id] == false
                }
            }
            notifyDataSetChanged()
            onSelectedStateChangedListener(value)
        }

    val selectedEntities : List<Entity>
    get() {
        val ids = selectedItems.filterValues { it }.keys
        return mEntities.filter { it.id in ids }
    }


    protected val uiHandler = Handler(Looper.getMainLooper())

    private var mEntities: MutableList<Entity> = CopyOnWriteArrayList<Entity>()

    private var visibleItems: List<Entity> = mEntities

    private var selectedItems = mutableMapOf<String, Boolean>()

    /**
     * Makes view clickable and sets click listener
     * Use [IBaseRecyclerAdapter.onCreateClickableViewHolder] to create ViewHolder
     */
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            onCreateClickableViewHolder(parent, viewType).apply {
                itemView.isClickable = true
                itemView.isFocusable = true
                itemView.isLongClickable = true
                itemView.setOnClickListener {
                    uiHandler.post {
                        itemClickListener.invoke(this)
                    }
                }
                itemView.setOnLongClickListener {
                    uiHandler.post {
                        itemLongClickListener.invoke(this)
                    }
                }
            }

    /**
     * [IFirebaseViewHolder.bind] used to bind a ViewHolder
     */
    final override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(visibleItems[position])


    /**
     * @return current selected items count
     *
     * @see IBaseRecyclerAdapter.setVisible
     * @see IBaseRecyclerAdapter.selectAll
     */
    final override fun getItemCount(): Int = visibleItems.size

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

    override fun set(entities: Collection<Entity>) {
        mEntities = entities.sorted().toMutableList()
        visibleItems = mEntities
        notifyDataSetChanged()
        return
        val ids = entities.map { it.id }
        mEntities.forEachIndexed { index, entity ->
            if (entity.id !in ids) {
                mEntities.remove(entity)
                uiHandler.post {
                    notifyItemRemoved(index)
                }
            }
        }
        addAll(entities)

    }

    /**
     * @see IBaseRecyclerAdapter.setVisible
     */
    final override fun setVisible(predicate: (Entity) -> Boolean): Int {
        isSearching = true
        visibleItems = mEntities.filter { predicate(it) }
        uiHandler.post {
            notifyDataSetChanged()
        }
        return visibleItems.size
    }

    final override fun setSelected(id: String, selected: Boolean) {
        if (!inSelectingMode) {
            inSelectingMode = true
        }

        val idx = mEntities.indexOfFirst { it.id == id }
        if (idx != -1) {
            selectedItems[id] = !(selectedItems[id] ?: false)
            notifyItemChanged(idx)
        }
    }

    override fun isSelected(id: String) : Boolean = selectedItems[id] == true

//    /**
//     * @see IBaseRecyclerAdapter.selectAll
//     */
//    final override fun selectAll() {
//        if (visibleItems !== mEntities) {
//            visibleItems = mEntities
//            uiHandler.post {
//                notifyDataSetChanged()
//            }
//            isSearching = false
//        }
//    }

    protected fun realItemCount(): Int = mEntities.size

    @CallSuper
    override fun add(entity: Entity, forceCallback:Boolean, byUser : Boolean): Int = synchronized(mEntities) {
        val idx = mEntities.indexOfFirst { it.id == entity.id }
        return if (idx != -1) {
            try {
                if (mEntities[idx] != entity) {
                    mEntities[idx] = entity
                    uiHandler.post {
                        if (!isSearching) {
                            notifyItemChanged(idx)
                        }
                        adapterCallback?.onItemChanged(entity)
                    }
                }
            } catch (ignore: Throwable) {
            }
            idx
        } else {
            try {
                mEntities.add(entity)
                mEntities.sort()
                val newIdx = mEntities.indexOf(entity)
                uiHandler.post {
                    if (!isSearching) {
                        notifyItemInserted(newIdx)
                    }
                }
                if (!isLoading || forceCallback) {
                    adapterCallback?.onItemAdded(entity)
                }
            } catch (ignore: Throwable) { }
            mEntities.size
        }
    }

    override fun addAll(entities: Collection<Entity>, forceCallback: Boolean, byUser: Boolean) =
        entities.forEach { add(it, forceCallback, byUser) }


//    @CallSuper
//    override fun insert(idx: Int, model: Entity,forceCallback: Boolean, byUser : Boolean) : Int = synchronized(_models) {
//        val findIdx = _models.indexOfFirst { it.id == model.id }
//        return if (findIdx != -1) {
//            if (_models[idx] != model) {
//                _models[idx] = model
//                uiHandler.post {
//                    if (!isSearching) {
//                        notifyItemChanged(idx)
//                    }
//                    adapterCallback?.onItemChanged(model)
//                }
//            }
//            findIdx
//        } else {
//            _models.add(idx,model)
//            uiHandler.post {
//                if (!isSearching) {
//                    notifyItemInserted(idx)
//                }
//                if (!isLoading || forceCallback) {
//                    adapterCallback?.onItemAdded(model)
//                }
//            }
//            idx
//        }
//
//    }

    @CallSuper
    override fun remove(id: String, byUser : Boolean): Boolean = synchronized(mEntities) {
        val idx = mEntities.indexOfFirst { it.id == id }
        if (idx != -1) {
            val model = mEntities.removeAt(idx)
            uiHandler.post {
                notifyItemRemoved(idx)
                adapterCallback?.onItemRemoved(model)
            }
            true
        } else false
    }

    @CallSuper
    protected fun removeAll()  = synchronized(mEntities) {
        mEntities.clear()
    }

    companion object {
        private val TAG = RealtimeDatabaseListRecyclerAdapter::class.java.simpleName
    }
}