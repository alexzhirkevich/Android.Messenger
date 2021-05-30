//package com.alexz.messenger.app.ui.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.alexz.messenger.app.ui.viewmodels.AboutViewModel
//import com.messenger.app.R
//
//class AboutFragment : Fragment() {
//    private lateinit var aboutViewModel: AboutViewModel
//    override fun onCreateView(inflater: LayoutInflater,
//                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        aboutViewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_about, container, false)
//        val textView = root.findViewById<TextView>(R.id.text_slideshow)
//        aboutViewModel.text.observe(viewLifecycleOwner, Observer { s -> textView.text = s })
//        return root
//    }
//}