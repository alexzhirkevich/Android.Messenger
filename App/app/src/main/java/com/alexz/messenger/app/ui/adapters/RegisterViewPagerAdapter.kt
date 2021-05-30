package com.alexz.test

import android.content.Context
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R

class RegisterViewPagerAdapter(context: Context) : RecyclerView.Adapter<RegisterViewPagerAdapter.RegisterViewPagerViewHolder>() {

    val texts : List<SpannableString>

    init {
        val tryNew = context.getString(R.string.try_new)
        val appName = context.getString(R.string.app_name)
        val tryNewSpannableString = SpannableString(Html.fromHtml(tryNew + appName))
        tryNewSpannableString.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.blue)),
                tryNew.length, tryNewSpannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        texts = listOf(tryNewSpannableString,tryNewSpannableString,tryNewSpannableString)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisterViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_register_pager_view,parent,false)

        return RegisterViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int = texts.size

    override fun onBindViewHolder(holder: RegisterViewPagerViewHolder, position: Int) {
        holder.bind(texts[position])
    }

    class RegisterViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView = itemView.findViewById<TextView>(R.id.text_view)

        fun bind(text : SpannableString){
            textView.text = text
        }
    }
}