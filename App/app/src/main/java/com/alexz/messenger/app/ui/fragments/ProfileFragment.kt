package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alexz.messenger.app.ui.views.AutoLinkTextView
import com.messenger.app.R

class ProfileFragment : Fragment() {

   // lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    = inflater.inflate(R.layout.fragment_profile, container, false)

//        searchView = requireParentFragment().requireView().findViewById<Toolbar>(R.id.toolbar).
//                findViewById<SearchView>(R.id.search).apply { isVisible = false }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.avatar_change).setOnClickListener {
            Toast.makeText(context,"change",Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.about_text).apply {
//            movementMethod = ScrollingMovementMethod()
        }

        view.findViewById<AutoLinkTextView>(R.id.about_text).apply {
            setLinkClickListener {
                Toast.makeText(context,"qwe",Toast.LENGTH_SHORT).show()
            }
            text = "$text\nдохуя\nдлинное\nописание"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile_self,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}