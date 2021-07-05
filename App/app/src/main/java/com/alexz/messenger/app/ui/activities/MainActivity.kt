package com.alexz.messenger.app.ui.activities

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.messenger.app.R

class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainActivity"
    }

    private val insetsChangedListeners = mutableListOf<OnSystemInsetsChangedListener>()

    var isFullscreen = false
    set(value)  {
        field = value
        if (value) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            supportActionBar?.hide()
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            supportActionBar?.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setWindowTransparency(object : OnSystemInsetsChangedListener{
            override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
                insetsChangedListeners.forEach {
                        it.onApplyWindowInsets(statusBarSize, navigationBarSize)
                }
            }
        })

    }

    fun addOnInsetsChangingListener(listener: OnSystemInsetsChangedListener)  =
        insetsChangedListeners.add(listener).also { requestUpdateInsets() }


    fun removeOnInsetsChangingListener(listener: OnSystemInsetsChangedListener) =
            insetsChangedListeners.remove(listener)



    open class EdgeToEdgeFragment : Fragment() , OnSystemInsetsChangedListener {

        var keyboardVisibility : Boolean = false
            private set

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {

        }

        open fun onKeyboardVisibilityChanged(visible : Boolean){

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            (activity as? MainActivity)?.addOnInsetsChangingListener(this)
            view.viewTreeObserver?.addOnGlobalLayoutListener(keyboardVisibilityListener)
        }

        override fun onDestroyView() {
            view?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardVisibilityListener)
            (activity as? MainActivity)?.removeOnInsetsChangingListener(this)
            super.onDestroyView()
        }

        private val keyboardVisibilityListener = ViewTreeObserver.OnGlobalLayoutListener {

            try {
                val displayRect = Rect().apply { requireView().getWindowVisibleDisplayFrame(this) }
                val keypadHeight = requireView().rootView.height - displayRect.bottom

                if (keyboardVisibility != keypadHeight > 500) {
                    keyboardVisibility = keypadHeight > 500
                    onKeyboardVisibilityChanged(keyboardVisibility)
                }
            }catch (t : Throwable){
                Log.wtf("KeyboardVisibilityListener",t)
            }
        }

    }
}


