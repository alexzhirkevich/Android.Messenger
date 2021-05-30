//package com.alexz.messenger.app.ui.fragments
//
//import android.app.Activity
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import android.widget.Toast
//import androidx.appcompat.widget.PopupMenu
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentContainerView
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.alexz.firerecadapter.ItemClickListener
//import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
//import com.alexz.messenger.app.data.entities.imp.Channel
//import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
//import com.alexz.messenger.app.data.entities.imp.Post
//import com.alexz.messenger.app.data.entities.imp.User
//import com.alexz.messenger.app.ui.adapters.PostRecyclerAdapter
//import com.alexz.messenger.app.ui.viewmodels.ChannelActivityViewModel
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
///**
// * A simple [Fragment] subclass.
// * Use the [ChannelPostsFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class ChannelPostsFragment : Fragment() {
//
//    private var channelDisposable : Disposable? = null
//    private var adminDisposable : Disposable? = null
//
//    private val viewModel: ChannelActivityViewModel by viewModels()
//    private var adapter: PostRecyclerAdapter? = null
//
//    private lateinit var mRecyclerView: RecyclerView
//    private var creationFab:FloatingActionButton? = null
//    private var fragmentContainer: FragmentContainerView? = null
//
//    private var channelAdmin : ChannelAdmin? = null
//    private var channel : Channel? = null
//
//    private val updateListener : (Channel) -> Unit = {
//        channel = it
//    }
//
//    override fun onResume() {
//        super.onResume()
//        adapter?.startListening()
//        creationFab?.setOnClickListener {
//            channel?.let {
//                    findNavController().navigate(R.id.action_posts_to_create,ChannelPostCreationFragment.newBundle(it))
//            }
//        }
//        creationFab?.setImageResource(R.drawable.ic_plus)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        adapter?.stopListening()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//
//        val view = inflater.inflate(R.layout.fragment_channel_posts, container, false)
//
//        mRecyclerView = view.findViewById(R.id.posts_recycler_view)
//        mRecyclerView.layoutManager = LinearLayoutManager(context)
//        arguments?.let {
//            channel = it.getParcelable(EXTRA_CHANNEL)
//        }
//        channel?.let { c ->
//            adapter = PostRecyclerAdapter(c.id, activity)
//            mRecyclerView.adapter = adapter
//            mRecyclerView.postDelayed({ mRecyclerView.smoothScrollToPosition(mRecyclerView.bottom) }, 200)
//
//            channel?.let { c ->
//                channelDisposable?.dispose()
//                channelDisposable = viewModel.getChannel(c.id)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                { updateListener(it) },
//                                {
//                                    // TODO: 15.05.2021 channel load error
//                                }
//                        )
//            }
//        }
//
//        adapter?.itemClickListener = object : ItemClickListener<Post> {
//            override fun onLongItemClick(viewHolder: FirebaseViewHolder<Post>): Boolean =
//                    onPostLongClick(viewHolder)
//        }
//
//        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (channelAdmin?.canPost == true){
//                    if (dy>0 ){
//                        creationFab?.hide()
//                    } else{
//                        creationFab?.show()
//                    }
//                }
//            }
//        })
//
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        adminDisposable?.dispose()
//        channel?.let {
//            adminDisposable = viewModel.getAdmins(it.id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                            { admList ->
//                                val userId = User().id
//                                channelAdmin = admList.find { admin -> admin.id == userId }
//                                if (channelAdmin?.canPost == true)
//                                    creationFab?.show()
//                                else
//                                    creationFab?.hide()
//                                activity?.invalidateOptionsMenu()
//                            },
//                            {})
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        channelDisposable?.dispose()
//    }
//
//    override fun onAttach(activity: Activity) {
//        super.onAttach(activity)
//        creationFab = activity.findViewById(R.id.posts_fab)
//        fragmentContainer = activity.findViewById(R.id.channel_fragment_container)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        if (channelAdmin?.canEdit == true) {
//            inflater.inflate(R.menu.menu_channel_admin, menu)
//        } else{
//            inflater.inflate(R.menu.menu_channel, menu)
//        }
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean =
//            when(item.itemId) {
//                android.R.id.home -> {
//                    activity?.finish()
//                    true
//                }
//                R.id.menu_channel_invite -> {
//                    if (channel != null && context != null) {
//                        val cm = getSystemService(requireContext(), ClipboardManager::class.java)
//                        val cd = ClipData.newPlainText("fm-channel-invite", viewModel.createInviteLink(channel!!.id))
//                        cm?.setPrimaryClip(cd)
//                        Toast.makeText(requireContext(), getString(R.string.action_link_copied), Toast.LENGTH_SHORT).show()
//                    }
//                    true
//                }
//                else -> super.onOptionsItemSelected(item)
//            }
//
//    fun onPostLongClick(viewHolder: FirebaseViewHolder<Post>): Boolean {
//        return viewHolder.entity?.let { post ->
//            try {
//                val pm = PopupMenu(requireContext(), viewHolder.itemView)
//                pm.gravity = Gravity.END
//
//                val menu = channelAdmin?.let { admin ->
//                    if (admin.canDelete || post.id == admin.id)
//                        R.menu.menu_post_admin
//                    else R.menu.menu_post
//                } ?: R.menu.menu_post
//                pm.inflate(menu)
//
//                pm.setOnMenuItemClickListener { e: MenuItem ->
//                    when (e.itemId) {
//                        R.id.post_share -> {
//                            TODO("post share")
//                        }
//                        R.id.post_delete -> {
//                            viewModel.deletePost(post).subscribeOn(Schedulers.io()).subscribe()
//                        }
//                        R.id.post_edit -> {
//                            TODO("post edit")
//                        }
//                    }
//                    false
//                }
//                pm.show()
//                true
//            } catch (t: Throwable) {
//                Log.e(javaClass.simpleName,"Error inflating post popup menu",t)
//                false
//            }
//        } ?: false
//    }
//
//
//    companion object CREATOR {
//
//        @JvmStatic
//        val EXTRA_CHANNEL = "EXTRA_CHANNEL"
//
//        @JvmStatic
//        val SAVE_ADAPTER_POS = "SAVE_ADAPTER_POS"
//
//        @JvmStatic
//        fun newInstance(channel: Channel) =
//                ChannelPostsFragment().apply {
//                    arguments = newBundle(channel)
//                }
//        @JvmStatic
//        fun newBundle(channel: Channel) = Bundle().apply {
//            putParcelable(EXTRA_CHANNEL, channel)
//        }
//    }
//}