package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.adapters.recycler.FullscreenContentAdapter
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.interfaces.IMediaContent
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentFullscreencontentBinding

class FullscreenContentFragment
    : MainActivity.EdgeToEdgeFragment<FragmentFullscreencontentBinding>() {

    companion object CREATOR {

        private const val EXTRA_CONTENT = "EXTRA_CONTENT"

        fun newBundle(content: List<IMediaContent>) : Bundle {
            return bundleOf(EXTRA_CONTENT to ArrayList(content))
        }
    }

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    private val content : List<IMediaContent> by lazy {
        requireArguments().getParcelableArrayList<IMediaContent>(EXTRA_CONTENT)!! }



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

