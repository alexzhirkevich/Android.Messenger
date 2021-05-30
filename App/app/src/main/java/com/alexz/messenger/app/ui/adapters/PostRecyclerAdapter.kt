//package com.alexz.messenger.app.ui.adapters
//
//import android.app.Activity
//import android.net.Uri
//import android.view.ViewGroup
//import com.alexz.firerecadapter.ItemClickListener
//import com.alexz.firerecadapter.firestore.FirestoreListRecyclerAdapter
//import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
//import com.alexz.messenger.app.data.entities.imp.Post
//import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
//import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
//import com.alexz.messenger.app.ui.views.PostView
//import com.alexz.messenger.app.util.FirebaseUtil.POSTS
//import com.alexz.messenger.app.util.FirebaseUtil.channelsCollection
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
//class PostRecyclerAdapter(private val channelId: String, var transitionActivity: Activity?=null) :
//        FirestoreListRecyclerAdapter<Post, PostRecyclerAdapter.PostViewHolder>(
//                Post::class.java,
//        channelsCollection.document(channelId).collection(POSTS)) {
//
//    private val imageClickListener: ItemClickListener<Post>? = null
//    private val avatarClickListener: ItemClickListener<Post>? = null
//    private val nameClickListener: ItemClickListener<Post>? = null
//
//
//
//    init {
////        adapterCallback = object : AdapterCallback<Post> {
////            override fun onItemAdded(item: Post) {
////                postDao.add(item)
////            }
////
////            override fun onItemRemoved(item: Post) {
////                postDao.delete(item.id)
////            }
////
////            override fun onItemChanged(item: Post) {
////                postDao.add(item)
////            }
////
////        }
//    }
//
//    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//        return PostViewHolder(PostView(parent.context))
//    }
//
//    inner class PostViewHolder(itemView: PostView) : FirebaseViewHolder<Post>(itemView) {
//
//        private val postView = itemView
//        private var disposable: Disposable?= null
//
//        override fun bind(entity: Post) {
//            super.bind(entity)
//            postView.bind(entity)
//        }
//
//        init {
//            disposable = channelsProvider.get(channelId)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(
//                            {
//                                postView.avatarView.setImageURI(Uri.parse(it.imageUri))
//                                postView.nameView.text = it.name
//                            },
//                            {
//                                // TODO: 15.05.2021 channel observe fail
//                            }
//                    )
//            postView.avatarView.setOnClickListener {
//                avatarClickListener?.onItemClick(this)
//            }
//            postView.avatarView.setOnLongClickListener {
//                if (avatarClickListener != null) {
//                    avatarClickListener.onLongItemClick(this)
//                    return@setOnLongClickListener true
//                }
//                false
//            }
//            postView.nameView.setOnClickListener {
//                nameClickListener?.onItemClick(this)
//            }
//            postView.nameView.setOnLongClickListener {
//                if (nameClickListener != null) {
//                    nameClickListener.onLongItemClick(this)
//                    return@setOnLongClickListener true
//                }
//                false
//            }
//            postView.contentGrid.setOnClickListener {
//                imageClickListener?.onItemClick(this)
//            }
//            postView.contentGrid.setOnLongClickListener {
//                if (imageClickListener != null) {
//                    imageClickListener.onLongItemClick(this)
//                    return@setOnLongClickListener true
//                }
//                false
//            }
//
//            transitionActivity?.let { postView.contentGrid.setFullscreenTransition(it) }
//        }
//    }
//
//    companion object{
//        val channelsProvider : ChannelsProvider by lazy { FirestoreChannelsProvider() }
//    }
//}