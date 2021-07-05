package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.alexz.messenger.app.ui.activities.MainActivity
import com.alexz.messenger.app.ui.views.setBottomMargin
import com.alexz.messenger.app.ui.views.setTopMargin
import com.messenger.app.R

class SearchFragment : MainActivity.EdgeToEdgeFragment() {

    private val toolbar : Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val searchView : SearchView by lazy { toolbar.findViewById<SearchView>(R.id.search) }
    private val recyclerView : RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        toolbar.setTopMargin(statusBarSize)
        recyclerView.setBottomMargin(navigationBarSize)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView.isIconified = false
        searchView.requestFocus()

        searchView.setOnCloseListener {
            exit()
            false
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