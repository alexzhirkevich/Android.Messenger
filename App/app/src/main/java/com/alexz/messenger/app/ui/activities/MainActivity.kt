package com.alexz.messenger.app.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.messenger.app.R

class MainActivity : AppCompatActivity() {

//    lateinit var fragmentController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_host_main) as NavHostFragment
//        fragmentController = navHostFragment.navController
    }

}
