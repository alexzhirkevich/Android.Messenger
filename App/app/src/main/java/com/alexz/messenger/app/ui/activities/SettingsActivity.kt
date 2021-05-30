//package com.alexz.messenger.app.ui.activities
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.preference.PreferenceFragmentCompat
//import com.messenger.app.R
//
//class SettingsActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.settings_activity)
//        if (savedInstanceState == null) {
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.settings, SettingsFragment())
//                    .commit()
//        }
//        val actionBar = supportActionBar
//        actionBar?.setDisplayHomeAsUpEnabled(true)
//    }
//
//    class SettingsFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }
//
//    companion object {
//        fun startActivity(context: Context) {
//            val intent = Intent(context, SettingsActivity::class.java)
//            context.startActivity(intent)
//        }
//    }
//}