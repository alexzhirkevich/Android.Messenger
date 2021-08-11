package com.community.messenger.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.flexbox.FlexboxLayout

class ContentBoxLayout @JvmOverloads constructor(
        context: Context, attributeSet: AttributeSet?=null,defStyleAttr : Int=0
) : FlexboxLayout(context,attributeSet,defStyleAttr) {

    private val views = mutableMapOf<Int,View>()
}