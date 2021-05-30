//package com.alexz.messenger.app.ui.activities
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.activity_fullscreen_image.*
//
//class FullscreenImageActivity : AppCompatActivity() {
//
//    private val viewModel : MediaViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_fullscreen_image)
//        supportActionBar?.apply {
//            setHomeButtonEnabled(true)
//            setDisplayHomeAsUpEnabled(true)
//        }
//        intent.getStringExtra(EXTRA_IMAGE_URI)?.let {
//            viewModel.loadImage(it)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({ bmp ->
//                        imageview_fullscreen.setImageBitmap(bmp)
//                    }, {
//                        Toast.makeText(this, getString(R.string.error_image_load), Toast.LENGTH_SHORT).show()
//                    })
//        }
//
//        imageview_fullscreen.setOnClickListener {
//            if (supportActionBar?.isShowing == true){
//                supportActionBar?.hide()
//            } else{
//                supportActionBar?.show()
//            }
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            onBackPressed()
//        }
//        return true
//    }
//
//    companion object {
//        private const val EXTRA_IMAGE_URI = "IMAGE_URI"
//        fun startActivity(context: Context, imageUri: String?, bundle: Bundle?) {
//            val intent = Intent(context, FullscreenImageActivity::class.java)
//            intent.putExtra(EXTRA_IMAGE_URI, imageUri)
//            if (bundle == null) {
//                context.startActivity(intent)
//            } else {
//                context.startActivity(intent, bundle)
//            }
//        }
//    }
//}