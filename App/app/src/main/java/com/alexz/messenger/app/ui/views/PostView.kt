//package com.alexz.messenger.app.ui.views
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.View
//import android.widget.RelativeLayout
//import android.widget.TextView
//import com.alexz.messenger.app.data.entities.interfaces.IPost
//import com.alexz.messenger.app.ui.common.contentgridlayout.ContentGridLayout
//import com.alexz.messenger.app.util.getTime
//import com.messenger.app.R
//
//class PostView @JvmOverloads constructor(
//        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
//        RelativeLayout(context, attrs, defStyleAttr) {
//
//    var avatarView : AvatarImageView
//        private set
//    var nameView : TextView
//        private set
//    var contentGrid : ContentGridLayout
//        private set
//    var textView : TextView
//        private set
//    var dateView : TextView
//        private set
//
//
//
//    fun bind(post: IPost){
//        contentGrid.clearContent()
//        post.content.forEach {
//            contentGrid.addContent(it)
//        }
//        if (contentGrid.content.isEmpty()){
//            contentGrid.visibility = View.GONE
//        } else {
//            contentGrid.visibility = View.VISIBLE
//            contentGrid.reGroup()
//        }
//        contentGrid.requestLayout()
//        textView.text = post.text
//        dateView.text = getTime(post.time)
//    }
//
//    fun setNameClickListener(nameClickListener: OnClickListener?) {
//        nameView.setOnClickListener { view: View? ->
//            nameClickListener?.onClick(view)
//        }
//    }
//
//    fun setAvatarClickListener(avatarClickListener: OnClickListener?) {
//        avatarView.setOnClickListener { view: View? ->
//            avatarClickListener?.onClick(view)
//        }
//    }
//
//    fun setNameLongClickListener(nameClickListener: OnLongClickListener?) {
//        nameView.setOnLongClickListener { view: View? ->
//            if (nameClickListener != null) {
//                return@setOnLongClickListener nameClickListener.onLongClick(view)
//            }
//            false
//        }
//    }
//
//    fun setAvatarLongClickListener(avatarClickListener: OnLongClickListener?) {
//        avatarView.setOnLongClickListener { view: View? ->
//            if (avatarClickListener != null) {
//                return@setOnLongClickListener avatarClickListener.onLongClick(view)
//            }
//            false
//        }
//    }
//
//    init {
//        inflate(context, R.layout.item_post, this)
//        avatarView = findViewById(R.id.post_avatar)
//        nameView = findViewById(R.id.post_name)
//        contentGrid = findViewById(R.id.post_grid_content)
//        textView = findViewById(R.id.post_text)
//        dateView = findViewById(R.id.post_date)
//    }
//
//}