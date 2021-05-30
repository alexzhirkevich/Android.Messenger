package com.alexz.test

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.alexz.messenger.app.ui.activities.enableWithEditTexts
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.messenger.app.R

class RegisterFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val btnCreateAccount = view.findViewById<Button>(R.id.create_account)
        val checkBoxRules = view.findViewById<CheckBox>(R.id.checkbox_rule)
        val txtRules = view.findViewById<TextView>(R.id.rules_2)
        val viewPager = view.findViewById<ViewPager2>(R.id.register_view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.register_tab_layout)
        val layoutInfo = view.findViewById<ViewGroup>(R.id._info_layout)
        val editName = view.findViewById<EditText>(R.id.name)
        val editEmail = view.findViewById<EditText>(R.id.email)
        val editPass = view.findViewById<EditText>(R.id.password)

        layoutInfo.apply {
            alpha = 0f
            animate()
                .setDuration(300)
                .setStartDelay(300)
                .alpha(1f)
                .start()
        }

        viewPager.apply {
            adapter = RegisterViewPagerAdapter(context)
            TabLayoutMediator(tabLayout, this, TabLayoutMediator.TabConfigurationStrategy{ _, _ -> }).attach()
        }

        txtRules.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                AlertDialog.Builder(context,R.style.DialogTheme)
                        .setView(LayoutInflater.from(context).inflate(R.layout.layout_terms,null,false))
                        .setTitle(getString(R.string.accept_rules_2))

                        .setPositiveButton(getString(R.string.accept)) { di, i ->
                            if (!checkBoxRules.isChecked) {
                                checkBoxRules.performClick()
                            }
                            di.cancel()
                        }
                        .setNegativeButton(getString(R.string.decline)) { di, i ->
                            if (checkBoxRules.isChecked) {
                                checkBoxRules.performClick()
                            }
                            di.cancel()
                        }
                        .show()
            }
        }

        btnCreateAccount.enableWithEditTexts(R.color.black,R.color.gray,{checkBoxRules.isChecked},
            editName,editEmail,editPass)

        checkBoxRules.apply {
            setOnClickListener {
                btnCreateAccount.apply {
                    isEnabled = isChecked && editPass.text.isNotEmpty()
                            && editName.text.isNotEmpty() && editEmail.text.isNotEmpty()
                    setTextColor(resources.getColor(if (isEnabled) R.color.black else R.color.gray))
                }
            }
        }

        return view
    }

}