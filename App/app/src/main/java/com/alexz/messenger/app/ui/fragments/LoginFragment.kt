package com.alexz.test

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.alexz.messenger.app.ui.activities.enableWithEditTexts
import com.messenger.app.R

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val editMail = view.findViewById<EditText>(R.id.email)
        val editPass = view.findViewById<EditText>(R.id.password)
        val btnLogin = view.findViewById<Button>(R.id.login)
        val imgLogo = view.findViewById<ImageView>(R.id.logo)
        val txtRegister = view.findViewById<TextView>(R.id.text_register)

        val transitionExtras = FragmentNavigatorExtras(
            imgLogo to getString(R.string.transition_logo),
            btnLogin to getString(R.string.transition_btn_signin)
        )

        txtRegister.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                findNavController().navigate(R.id.action_login_to_register,null,null,transitionExtras)
            }
        }

        btnLogin.apply {
            enableWithEditTexts(R.color.black, R.color.gray, { true }, editMail, editPass)
            setOnClickListener {
                findNavController().navigate(R.id.action_login_to_bottom_navigation)
            }
        }

        return view
    }


}