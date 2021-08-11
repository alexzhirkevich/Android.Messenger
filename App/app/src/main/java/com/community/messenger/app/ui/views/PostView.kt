package com.community.messenger.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.community.messenger.common.entities.interfaces.IPost
import com.community.messenger.app.R
import com.community.messenger.app.databinding.ItemPostBinding

class PostView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {


    init {
        inflate(context, R.layout.item_post, this)
    }
    val binding : ItemPostBinding = ItemPostBinding.bind(this)


    fun bind(post: IPost){

    }

}