//package com.alexz.messenger.app.ui.fragments
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.*
//import android.widget.EditText
//import android.widget.ProgressBar
//import androidx.appcompat.widget.PopupMenu
//import androidx.core.app.ActivityOptionsCompat
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.alexz.firerecadapter.ItemClickListener
//import com.alexz.firerecadapter.LoadingCallback
//import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
//import com.alexz.messenger.app.data.entities.imp.Chat
//import com.alexz.messenger.app.ui.activities.ChatActivity
//import com.alexz.messenger.app.ui.adapters.ChatRecyclerAdapter
//import com.alexz.messenger.app.ui.dialogwindows.AddChatDialog
//import com.alexz.messenger.app.ui.viewmodels.ChatActivityViewModel
//import com.alexz.messenger.app.util.KeyboardUtil
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.messenger.app.R
//
//class ChatsFragment : Fragment(), ItemClickListener<Chat> {
//
//    lateinit var fab: FloatingActionButton
//
//    private lateinit var adapter: ChatRecyclerAdapter
//    private var drawerLayout: DrawerLayout? = null
//    private var editSearch: EditText? = null
//    private lateinit var dialogRecyclerView: RecyclerView
//    private lateinit var addChatDialog: AddChatDialog
//
//    private val viewModel : ChatActivityViewModel by viewModels()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_chats, container, false)
//        drawerLayout = activity?.findViewById(R.id.drawer_layout)
//        setupRecyclerView(view)
//        setupFloatingButton()
//        setupToolbar()
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        adapter.startListening()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        adapter.stopListening()
//
//    }
//
//    override fun onResume() {
//        fab.setOnClickListener {
//            addChatDialog = AddChatDialog(this)
//            addChatDialog.show()
//        }
//        super.onResume()
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//        fab.setOnClickListener {}
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == AddChatDialog.REQ_NEW_CHAT_PHOTO) {
//            addChatDialog.onDialogResult(requestCode, resultCode, data)
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
//                drawerLayout?.closeDrawer(GravityCompat.START)
//            } else drawerLayout?.openDrawer(GravityCompat.START)
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
//    override fun onItemClick(viewHolder: FirebaseViewHolder<Chat>) {
//        if (context != null && activity != null) {
//            if (viewHolder is ChatRecyclerAdapter.ChatViewHolder) {
//                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
//                        viewHolder.imgView, getString(R.string.util_transition_toolbar_image))
//                ChatActivity.startActivity(requireContext(), viewHolder.entity, options.toBundle())
//            }
//        }
//    }
//
//    override fun onLongItemClick(viewHolder: FirebaseViewHolder<Chat>): Boolean {
//        activity?.let { activity ->
//            val pm = PopupMenu(activity, viewHolder.itemView)
//            pm.gravity = Gravity.RIGHT
//            pm.inflate(R.menu.menu_dialogs)
//            pm.setOnMenuItemClickListener {
//                if (it.itemId == R.id.message_delete ) {
//                    viewHolder.entity?.let { chat -> viewModel.delete(chat) }
//                    return@setOnMenuItemClickListener true
//                }
//                false
//            }
//            pm.show()
//            return true
//        }
//        return false
//    }
//
//    private fun setupToolbar() {
//        editSearch = activity?.findViewById(R.id.edit_search)
//        editSearch?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                if (charSequence.isEmpty()) {
//                    adapter.selectAll()
//                } else {
//                    if (adapter.select(charSequence.toString()) == 0) {
//                        editSearch?.error = resources.getString(R.string.error_notfound)
//                    }
//                }
//            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        })
//    }
//
//    private fun setupRecyclerView(view: View) {
//        val loadingPb = view.findViewById<ProgressBar>(R.id.dialog_loading_pb)
//        dialogRecyclerView = view.findViewById(R.id.dialog_rec_view)
//        dialogRecyclerView.layoutManager = LinearLayoutManager(activity)
//        adapter = ChatRecyclerAdapter()
//        dialogRecyclerView.adapter = adapter
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
//                dialogRecyclerView.setOnScrollChangeListener { _: View?, _: Int, i1: Int, _: Int, i3: Int ->
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
//}