package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alexz.messenger.app.data.model.imp.MediaContent
import com.alexz.messenger.app.data.model.interfaces.IMediaContent
import com.alexz.messenger.app.ui.common.contentgridlayout.ContentGridLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.app.R

class ChannelsFragment : Fragment() {
    private var floatingActionButton: FloatingActionButton? = null
    private var contentGridLayout: ContentGridLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_channels, container, false)
        contentGridLayout = view.findViewById(R.id.post_grid_content)
        contentGridLayout?.addContent(MediaContent(IMediaContent.VIDEO,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
        contentGridLayout?.addContent(MediaContent(IMediaContent.IMAGE,
                "https://firebasestorage.googleapis.com/v0/b/messenger-302121.appspot.com/o/FetFyNi4VXPurgFEkN24risaFQv1%2F1614521613228.jpg?alt=media&token=ae192601-de34-466b-b374-bc2ef3521289"
        ))
        contentGridLayout?.reGroup()
        contentGridLayout?.setFullscreenTransition(activity)
        floatingActionButton = activity?.findViewById(R.id.fab_dialogs)

        return view
    }

    override fun onResume() {
        floatingActionButton?.setOnClickListener { e: View? ->
            contentGridLayout?.addContent(MediaContent(IMediaContent.IMAGE,
                    "https://firebasestorage.googleapis.com/v0/b/messenger-302121.appspot.com/o/FetFyNi4VXPurgFEkN24risaFQv1%2F1614521613228.jpg?alt=media&token=ae192601-de34-466b-b374-bc2ef3521289"
            ))
            contentGridLayout?.reGroup()
        }
        super.onResume()
    }
}