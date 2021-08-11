package com.community.messenger.app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentSearchBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.views.setBottomMargin
import com.community.messenger.app.ui.views.setTopMargin

class   SearchFragment : MainActivity.EdgeToEdgeFragment<FragmentSearchBinding>() {

    private val searchView : SearchView by lazy { binding.toolbar.findViewById<SearchView>(R.id.search_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        with(binding) {
            toolbar.setTopMargin(statusBarSize)
            recyclerView.setBottomMargin(navigationBarSize)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView.apply {

            searchView.setOnCloseListener {
                exit()
                false
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
            isIconified = false
            requestFocus()
        }

//        searchView.setOnQueryTextFocusChangeListener { _, focused ->
//            if (!focused){
//                exit()
//            }
//        }

        addOnBackPressedListener {
            if (!searchView.isIconified){
                searchView.isIconified = true
            }
            exit()
            false
        }
    }

    private fun exit(){
        parentFragmentManager.popBackStack()
    }
}