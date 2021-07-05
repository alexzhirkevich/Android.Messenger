package com.alexz.messenger.app.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.firerecadapter.viewholder.BaseViewHolder
import com.alexz.messenger.app.data.entities.interfaces.IMediaContent
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.views.setTopMargin
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ui.PlayerView
import com.messenger.app.R

class FullscreenContentFragment : MainActivity.EdgeToEdgeFragment(){

    companion object CREATOR {

        private const val EXTRA_CONTENT = "EXTRA_CONTENT"

        fun newBundle(content: List<IMediaContent>) : Bundle {
            return bundleOf().apply { putParcelableArrayList(EXTRA_CONTENT, ArrayList(content))}
        }
    }

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    private val content : List<IMediaContent> by lazy {
        requireArguments().getParcelableArrayList<IMediaContent>(EXTRA_CONTENT)!! }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_fullscreencontent,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView.apply {
            adapter = FullscreenContentAdapter().apply {
                set(content)
                itemClickListener = this@FullscreenContentFragment::onItemClick
            }
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        }
        addOnBackPressedListener {
            exit()
            false
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).apply {
            isFullscreen = false
        }
    }

    override fun onResume() {
        super.onResume()
        setToolbar(toolbar.apply { title = getString(R.string.photo) })
        setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fullscreen_content,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> exit()
        }
        return false
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        super.onApplyWindowInsets(statusBarSize, navigationBarSize)

       toolbar.setTopMargin(statusBarSize)
    }

    private fun onItemClick(viewHolder: FullscreenContentAdapter.FullscreenContentViewHolder) {
        (activity as MainActivity).apply {
            isFullscreen = !isFullscreen
        }

    }

    private fun exit(){
        parentFragmentManager.popBackStack()
    }
}

class FullscreenContentAdapter : BaseRecyclerAdapter<IMediaContent,FullscreenContentAdapter.FullscreenContentViewHolder>() {

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): FullscreenContentViewHolder {
        val view = if (viewType == IMediaContent.IMAGE)
            SubsamplingScaleImageView(parent.context).apply {
                setDoubleTapZoomDuration(resources.getInteger(R.integer.anim_duration_medium))
                setDoubleTapZoomScale(1.5f)
            }
        else
            PlayerView(parent.context)

        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        return FullscreenContentViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int =
            entities[position].type

    class FullscreenContentViewHolder(view: View) : BaseViewHolder<IMediaContent>(view) {

        override fun bind(entity: IMediaContent) {
            super.bind(entity)

            if (entity.type == IMediaContent.IMAGE && itemView is SubsamplingScaleImageView) {

                Glide.with(itemView.context).asBitmap().load(entity.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .addListener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
                                                      isFirstResource: Boolean): Boolean {
                                Log.e(this.javaClass.simpleName, "Failed to load image\n${e.toString()}")
                                return true
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?,
                                                         target: Target<Bitmap>?, dataSource: DataSource?,
                                                         isFirstResource: Boolean): Boolean {
                                if (resource != null) {
                                    itemView.post {
                                        itemView.setImage(ImageSource.bitmap(resource))
                                    }
                                    return true

                                }
                                return false
                            }
                        }).submit()
            }
        }
    }
}