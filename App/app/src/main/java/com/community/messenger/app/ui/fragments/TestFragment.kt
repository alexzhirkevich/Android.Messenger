package com.community.messenger.app.ui.fragments

import com.community.messenger.app.databinding.FragmentTestBinding
import com.community.messenger.app.ui.activities.MainActivity
import com.community.messenger.app.ui.views.setTopMargin

class TestFragment : MainActivity.EdgeToEdgeFragment<FragmentTestBinding>() {

    override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
        binding.root.setTopMargin(statusBarSize)
    }
}