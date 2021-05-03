package com.alexz.messenger.app.ui.fragments

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.repo.ChannelsRepository
import com.alexz.messenger.app.ui.adapters.PostRecyclerAdapter
import com.alexz.messenger.app.ui.viewmodels.ChannelActivityViewModel
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.app.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable

/**
 * A simple [Fragment] subclass.
 * Use the [ChannelPostsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChannelPostsFragment : Fragment() {

    var channelDispose : Disposable? = null

    private var channel : Channel? = null
    private var adapter: PostRecyclerAdapter? = null

    private lateinit var mRecyclerView: RecyclerView
    private var creationFab:FloatingActionButton? = null
    private var fragmentContainer: FragmentContainerView? = null

    private val updateListener : (Channel) -> Unit = {
        channel = it
        if (it.admins[FirebaseUtil.currentFireUser?.uid]?.canPost == true){
            creationFab?.show()
        } else{
            creationFab?.hide()
        }
        activity?.invalidateOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
        creationFab?.setOnClickListener {
            channel?.let {
                    findNavController().navigate(R.id.action_posts_to_create,ChannelPostCreationFragment.newBundle(it))
            }
        }
        creationFab?.setImageResource(R.drawable.ic_plus)
    }

    override fun onPause() {
        super.onPause()
        adapter?.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_channel_posts, container, false)
        val viewModel : ChannelActivityViewModel by viewModels()

        mRecyclerView = view.findViewById(R.id.posts_recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        arguments?.let {
            channel = it.getParcelable(EXTRA_CHANNEL)
        }
        channel?.let {
            adapter = PostRecyclerAdapter(it.id, activity)
            mRecyclerView.adapter = adapter
            mRecyclerView.postDelayed({ mRecyclerView.smoothScrollToPosition(mRecyclerView.bottom) }, 200)
            if (it.admins[FirebaseUtil.currentFireUser?.uid]?.canPost == true) {
                creationFab?.visibility = View.VISIBLE
            }

            channel?.let { c ->
                channelDispose?.dispose()
                channelDispose = viewModel.getChannel(c.id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { updateListener(it) }
                        .subscribe()
            }

        }

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (channel?.admins?.get(User().id)?.canPost == true){
                    if (dy>0 ){
                        creationFab?.hide()
                    } else{
                        creationFab?.show()
                    }
                }
            }
        })
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        channelDispose?.dispose()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        creationFab = activity.findViewById(R.id.posts_fab)
        fragmentContainer = activity.findViewById(R.id.channel_fragment_container)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        if (channel?.admins?.get(User().id)?.canEdit == true) {
            inflater.inflate(R.menu.menu_channel_admin, menu)
        } else{
            inflater.inflate(R.menu.menu_channel, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        } else {
            if (item.itemId == R.id.menu_channel_invite) {
                if (channel != null && context != null) {
                    val cm = getSystemService(requireContext(), ClipboardManager::class.java)
                    val cd = ClipData.newPlainText("fm-channel-invite", ChannelsRepository.createInviteLink(channel!!.id))
                    cm?.setPrimaryClip(cd)
                    Toast.makeText(requireContext(), getString(R.string.action_link_copied), Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
        return true
    }

    companion object CREATOR {

        @JvmStatic
        val EXTRA_CHANNEL = "EXTRA_CHANNEL"

        @JvmStatic
        val SAVE_ADAPTER_POS = "SAVE_ADAPTER_POS"

        @JvmStatic
        fun newInstance(channel: Channel) =
                ChannelPostsFragment().apply {
                    arguments = newBundle(channel)
                }
        @JvmStatic
        fun newBundle(channel: Channel) = Bundle().apply {
            putParcelable(EXTRA_CHANNEL, channel)
        }
    }
}