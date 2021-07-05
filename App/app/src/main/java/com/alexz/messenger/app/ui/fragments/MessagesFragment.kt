package com.alexz.messenger.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexz.messenger.app.ui.activities.MainActivity
import com.messenger.app.R

class MessagesFragment : MainActivity.EdgeToEdgeFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? 
        = inflater.inflate(R.layout.fragment_messages,container,false)
}