//package com.alexz.messenger.app.ui.fragments
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentContainerView
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.alexz.messenger.app.data.entities.imp.Channel
//import com.alexz.messenger.app.data.entities.imp.MediaContent
//import com.alexz.messenger.app.data.entities.imp.Post
//import com.alexz.messenger.app.data.entities.interfaces.IMediaContent
//import com.alexz.messenger.app.data.repo.UiHandler
//import com.alexz.messenger.app.ui.activities.MediaViewModel
//import com.alexz.messenger.app.ui.common.contentgridlayout.ContentGridLayout
//import com.alexz.messenger.app.ui.viewmodels.ChannelActivityViewModel
//import com.alexz.messenger.app.ui.views.AvatarImageView
//import com.alexz.messenger.app.util.FirebaseUtil
//import com.alexz.messenger.app.util.MetrixUtil
//import com.alexz.messenger.app.util.getTime
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//
///**
// * A simple [Fragment] subclass.
// * Use the [ChannelPostCreationFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class ChannelPostCreationFragment : Fragment() {
//
//    var channelDispose : Disposable? = null
//
//    private var channel : Channel? = null
//
//    private var fragmentContainer: FragmentContainerView? = null
//    private var creationFab: FloatingActionButton? = null
//
//    lateinit var avatarView : AvatarImageView
//    lateinit var nameView : TextView
//    lateinit var contentGrid : ContentGridLayout
//    lateinit var textView : EditText
//    lateinit var dateView : TextView
//    lateinit var mediaBtn : Button
//
//    val viewModel : MediaViewModel by viewModels()
//
//    private val updateListener : (Channel) -> Unit = {
//        UiHandler.post {
//            channel = it
//            if (it.admins[FirebaseUtil.currentFireUser?.uid]?.canPost == false) {
//                Toast.makeText(context, R.string.msg_cant_post_no_more, Toast.LENGTH_LONG).show()
//                if (isAdded) {
//                    creationFab?.hide()
//                    findNavController().navigateUp()
//                }
//            }
//            avatarView.setImageURI(Uri.parse(it.imageUri))
//            nameView.text = it.name
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            channel = it.getParcelable(EXTRA_CHANNEL)
//        }
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_channel_post_creation, container, false)
//        val viewModel : ChannelActivityViewModel by viewModels()
//
//        avatarView = view.findViewById(R.id.post_mutable_avatar)
//        nameView = view.findViewById(R.id.post_mutable_name)
//        contentGrid = view.findViewById(R.id.post_mutable_grid_content)
//        textView = view.findViewById(R.id.post_mutable_text)
//        dateView = view.findViewById(R.id.post_mutable_date)
//        mediaBtn = view.findViewById(R.id.btn_add_media)
//
//        channel?.let { c ->
//            nameView.text = c.name
//            avatarView.setImageURI(Uri.parse(c.imageUri))
//            channelDispose?.dispose()
//            channelDispose = viewModel.getChannel(c.id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                            { updateListener(it) },
//                            { // TODO: 15.05.2021 channel load error
//                            }
//                    )
//        }
//        dateView.text = getTime(System.currentTimeMillis())
//
//        mediaBtn.setOnClickListener {
//            val photoPickerIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
//            startActivityForResult(photoPickerIntent, REQ_MEDIA_CONTENT)
//        }
//
//        return view
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        channelDispose?.dispose()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQ_MEDIA_CONTENT && resultCode == Activity.RESULT_OK) {
//            data?.data?.let { uri ->
//                viewModel.uploadImage(uri)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                {
//                                    if (it?.second != null) {
//                                        val content = MediaContent(IMediaContent.IMAGE, it.second.toString())
//                                        contentGrid.apply {
//                                            addContent(content)
//                                            layoutParams.height = MetrixUtil.dpToPx(context, 350)
//                                            requestLayout()
//                                            reGroup()
//                                        }
//                                    }
//                                },
//                                {
//                                    Toast.makeText(context,getString(R.string.error_image_upload),Toast.LENGTH_SHORT).show()
//                                }
//                        )
//            }
//        }
//    }
//
//    override fun onAttach(activity: Activity) {
//        super.onAttach(activity)
//        creationFab = activity.findViewById(R.id.posts_fab)
//        fragmentContainer = activity.findViewById(R.id.channel_fragment_container)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        creationFab?.setImageResource(R.drawable.ic_done)
//        creationFab?.setOnClickListener { _ ->
//            channel?.let { ch ->
//                val viewModel: ChannelActivityViewModel by viewModels()
//                val p = Post(channelId = ch.id, text = textView.text.toString())
//                contentGrid.content.forEach { p.content.add(it as MediaContent) }
//                viewModel.createPost(p)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                {
//                                    findNavController().navigateUp()
//                                },
//                                {t->
//                                    Toast.makeText(context, getString(R.string.error_post_creation), Toast.LENGTH_LONG).show()
//                                    Log.e("POST CREATION FAIL",t.toString())
//                                }
//                        )
//            }
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean =
//       when(item.itemId) {
//           android.R.id.home -> {
//               findNavController().navigateUp()
//               true
//           }
//           else -> super.onOptionsItemSelected(item)
//       }
//
//    companion object CREATOR {
//
//        @JvmStatic
//        val REQ_MEDIA_CONTENT = 1293
//
//        @JvmStatic
//        val EXTRA_CHANNEL = "EXTRA_CHANNEL"
//
//        @JvmStatic
//        fun newInstance(channel: Channel) =
//                ChannelPostCreationFragment().apply {
//                    arguments = newBundle(channel)
//                }
//
//        @JvmStatic
//        fun newBundle(channel: Channel) = Bundle().apply {
//            putParcelable(EXTRA_CHANNEL, channel)
//        }
//    }
//}