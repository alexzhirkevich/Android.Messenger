package com.alexz.test

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.messenger.app.R

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        view.findViewById<ImageView>(R.id.avatar_change).setOnClickListener {
            Toast.makeText(context,"change",Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.about_text).apply {
//            movementMethod = ScrollingMovementMethod()
        }

        view.findViewById<TextView>(R.id.about_text).apply {
            text = text.toString() + "\nдохуя\nдлинное\nописание"
        }

        return view
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