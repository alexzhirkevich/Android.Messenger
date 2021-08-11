package com.community.recadapter

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.messenger.common.entities.interfaces.IEntity
import java.util.concurrent.CopyOnWriteArrayList


abstract class AsyncRecyclerAdapter<Entity : IEntity, PH : PlaceHolder<Entity>>
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    IAsyncRecyclerAdapter<Entity, PH> {

    override var itemClickListener: ItemClickListener<PH> = {}
    override var itemLongClickListener: ItemLongClickListener<PH> = { true }
    override var adapterCallback: AdapterCallback<Entity> = object : AdapterCallback<Entity> {}
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
                    selectedItems[it.id] = false
                }
            }
            notifyDataSetChanged()
            onSelectedStateChangedListener(value)
        }

    val selectedEntities: List<Entity>
        get() {
            val ids = selectedItems.filterValues { it }.keys
            return mEntities.filter { it.id in ids }
        }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return entities[position].id.hashCode().toLong()
    }

    protected val uiHandler = Handler(Looper.getMainLooper())

    private var mEntities: MutableList<Entity> = CopyOnWriteArrayList<Entity>()

    private var visibleItems: List<Entity> = mEntities

    private var selectedItems = mutableMapOf<String, Boolean>()

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(onCreatePlaceHolder(parent, viewType).apply {
            isClickable = true
            isFocusable = true
            isLongClickable = true
            setOnClickListener {
                uiHandler.post {
                    itemClickListener.invoke(this)
                }
            }
            setOnLongClickListener {
                uiHandler.post {
                    itemLongClickListener.invoke(this)
                }
            }
        }){}
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        (holder.itemView as PlaceHolder<Entity>).apply {
            entity = visibleItems[pos]
            position = pos
            bind(entity!!)
        }
    }

    final override fun getItemCount(): Int = visibleItems.size

    fun clear() {
        val size = itemCount
        removeAll()
        uiHandler.post {
           notifyItemRangeRemoved(0,size)
        }
    }

    override suspend fun set(entities: Collection<Entity>) {

        val asList = entities.sorted().toMutableList()
        if (!isSearching) {
            val diff = calculateDiff(mEntities, asList)
            mEntities = asList
            visibleItems = mEntities
            diff.dispatchUpdatesTo(this)
        } else{
            mEntities = asList
            visibleItems = mEntities
        }
       // notifyDataSetChanged()
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
        entities.forEach { add(it) }
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


    protected fun realItemCount(): Int = mEntities.size

    @CallSuper
    override fun add(entity: Entity): Int = synchronized(mEntities) {
        val idx = mEntities.indexOfFirst { it.id == entity.id }
        return if (idx != -1) {
            try {
                if (mEntities[idx] != entity) {
                    mEntities[idx] = entity
                    uiHandler.post {
                        if (!isSearching) {
                            notifyItemChanged(idx)
                        }
                        adapterCallback.onItemChanged(entity)
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
                if (!isLoading) {
                    adapterCallback.onItemAdded(entity)
                }
            } catch (ignore: Throwable) { }
            mEntities.size
        }
    }


    @CallSuper
    override fun remove(id: String): Boolean = synchronized(mEntities) {
        val idx = mEntities.indexOfFirst { it.id == id }
        if (idx != -1) {
            val model = mEntities.removeAt(idx)
            uiHandler.post {
                notifyItemRemoved(idx)
                adapterCallback.onItemRemoved(model)
            }
            true
        } else false
    }

    @CallSuper
    protected fun removeAll()  = synchronized(mEntities) {
        mEntities.clear()
    }

    private fun calculateDiff(old : List<Entity>, new : List<Entity>): DiffUtil.DiffResult=
        DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun getOldListSize(): Int =
                old.size


            override fun getNewListSize(): Int =
                new.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition] == new[newItemPosition]


            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(oldItemPosition,newItemPosition)
        })


}

