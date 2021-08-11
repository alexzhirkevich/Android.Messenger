//package com.community.messenger.app.ui.fragments
//
//import android.app.Activity
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.core.app.ActivityOptionsCompat
//import androidx.core.view.GravityCompat
//import androidx.core.widget.doOnTextChanged
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.community.firerecadapter.ItemClickListener
//import com.community.firerecadapter.LoadingCallback
//import com.community.firerecadapter.viewholder.FirebaseViewHolder
//import com.community.messenger.common.entities.imp.Channel
//import com.community.messenger.app.ui.activities.ChannelActivity
//import com.community.messenger.app.ui.adapters.recycler.ChannelRecyclerAdapter
//import com.community.messenger.common.util.KeyboardUtil
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.community.messenger.app.R
//
//class ChannelsFragment : Fragment(), ItemClickListener<Channel> {
//
//    private lateinit var fab: FloatingActionButton
//
//    private lateinit var adapter: ChannelRecyclerAdapter
//    private lateinit var drawerLayout: DrawerLayout
//    private var editSearch: EditText? = null
//    private lateinit var channelRecyclerView: RecyclerView
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_channels, container, false)
//        setupToolbar()
//        setupRecyclerView(view)
//        setupFloatingButton()
//        return view
//    }
//
//    override fun onStart() {
//        adapter.startListening()
//        super.onStart()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        adapter.stopListening()
//
//    }
//    override fun onPause() {
//        super.onPause()
//        fab.setOnClickListener{}
//    }
//
//    override fun onResume() {
//        super.onResume()
//        fab.setOnClickListener{
//            Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START)
//            } else drawerLayout.openDrawer(GravityCompat.START)
//            R.id.action_search -> if (editSearch?.visibility == View.VISIBLE) {
//                editSearch?.visibility = View.GONE
//                editSearch?.setText("")
//                editSearch?.layoutParams?.width = 0
//                editSearch?.requestLayout()
//                adapter.selectAll()
//                if (!KeyboardUtil.hasHardwareKeyboard(context)) {
//                    KeyboardUtil.hideKeyboard(editSearch)
//                }
//            } else {
//                editSearch?.visibility = View.VISIBLE
//                editSearch?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
//                editSearch?.requestLayout()
//                if (!KeyboardUtil.hasHardwareKeyboard(context)) {
//                    KeyboardUtil.showKeyboard(editSearch)
//                }
//                editSearch?.requestFocus()
//            }
//        }
//        return true
//    }
//
//    private fun setupToolbar() {
//        editSearch = activity?.findViewById(R.id.edit_search)
//        editSearch?.doOnTextChanged { text, _, _, _ ->
//            if (text == null || text.isEmpty()) {
//                adapter.selectAll()
//            } else {
//                if (adapter.select(text.toString()) == 0) {
//                    editSearch?.error = resources.getString(R.string.error_notfound)
//                }
//            }
//        }
//    }
//
//    private fun setupRecyclerView(view: View) {
//        val loadingPb = view.findViewById<ProgressBar>(R.id.channel_loading_pb)
//        channelRecyclerView = view.findViewById(R.id.channel_rec_view)
//        channelRecyclerView.layoutManager = LinearLayoutManager(activity)
//        adapter = ChannelRecyclerAdapter()
//        channelRecyclerView.adapter = adapter
//        adapter.itemClickListener = this
//        adapter.loadingCallback = object : LoadingCallback {
//            override fun onStartLoading() {
//                loadingPb.visibility = View.VISIBLE
//            }
//
//            override fun onEndLoading() {
//                loadingPb.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun setupFloatingButton() {
//        val activity: Activity? = activity
//        if (activity != null) {
//            fab = activity.findViewById(R.id.fab_dialogs)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                channelRecyclerView.setOnScrollChangeListener { _: View?, _: Int, i1: Int, _: Int, i3: Int ->
//                    if (adapter.itemCount > 10) {
//                        if (i1 < i3) {
//                            fab.show()
//                        } else {
//                            fab.hide()
//                        }
//                    } else fab.show()
//                }
//            }
//        }
//    }
//
//    override fun onItemClick(viewHolder: FirebaseViewHolder<Channel>) {
//        viewHolder.entity.let {
//            if (viewHolder is ChannelRecyclerAdapter.ChannelViewHolder) {
//                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
//                        viewHolder.imgView, getString(R.string.util_transition_toolbar_image))
//                if (context != null) {
//                    ChannelActivity.startActivity(requireContext(), it,options.toBundle())
//                } }
//        }
//    }
//}