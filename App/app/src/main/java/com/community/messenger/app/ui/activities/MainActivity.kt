package com.community.messenger.app.ui.activities

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.community.messenger.app.R
import com.community.messenger.common.util.KeyboardUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

class MainActivity : AppCompatActivity() {

    interface KeyboardVisibilityHandler {
        val isKeyboardVisible : Boolean
        fun onKeyboardVisibilityChanged(visible : Boolean)
    }

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

    private val insetsChangedListeners = mutableListOf<SystemInsetsChangedListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setWindowTransparency(object : SystemInsetsChangedListener{
            override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {
                insetsChangedListeners.forEach {
                        it.onApplyWindowInsets(statusBarSize, navigationBarSize)
                }
            }
        })

    }

    fun addOnInsetsChangingListener(listener: SystemInsetsChangedListener)  =
        insetsChangedListeners.add(listener).also { requestUpdateInsets() }


    fun removeOnInsetsChangingListener(listener: SystemInsetsChangedListener) =
            insetsChangedListeners.remove(listener)


    open class EdgeToEdgeFragment<Binding : ViewBinding>
        : BindingFragment<Binding>() , SystemInsetsChangedListener, KeyboardVisibilityHandler {

        override var isKeyboardVisible : Boolean
            get() = mIsKeyboardVisible
            set(value) {
                if (mIsKeyboardVisible ==value)
                    return
                if (value){
                    KeyboardUtils.showKeyboard(requireView())
                } else{
                    KeyboardUtils.hideKeyboard(requireView())
                }
                mIsKeyboardVisible = value
            }

        private var mIsKeyboardVisible : Boolean = false
            set(value) {
                if (field != value) {
                    onKeyboardVisibilityChanged(value)
                    field = value
                }
            }

        override fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int) {

        }

        override fun onKeyboardVisibilityChanged(visible : Boolean){

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

            if (isResumed) {
                val displayRect = Rect().apply {
                    requireView().getWindowVisibleDisplayFrame(this) }
                val keypadHeight = requireView().rootView.height - displayRect.bottom

                mIsKeyboardVisible = keypadHeight > 500
            }
        }
    }
}

open class BindingFragment<Binding : ViewBinding> : Fragment() {

    lateinit var binding : Binding
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var clazz: Class<*> = javaClass
        var inflateParameters: Pair<Class<*>, Method>? = null

        while (true) {
            try {
                inflateParameters =
                    (clazz.genericSuperclass as ParameterizedType).actualTypeArguments.mapNotNull {
                        try {
                            (it as Class<*>) to it.getMethod(
                                "inflate",
                                LayoutInflater::class.java,
                                ViewGroup::class.java,
                                Boolean::class.java
                            )
                        } catch (e: Throwable) {
                            null
                        }
                    }.first()
                break
            } catch (t: Throwable) {
                if (clazz.superclass == null) {
                    throw throw Exception("Invalid binding")
                }
                clazz = clazz.superclass
            }
        }
        if (inflateParameters == null) {
            throw throw Exception("Invalid binding")
        }
        binding = inflateParameters.second(
            inflateParameters.first,
            inflater,
            container,
            false
        ) as Binding

//        val inflateMethod = clazzz.getMethod("inflate",
//            LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
//        binding = inflateMethod.invoke(clazzz,inflater,container,false) as Binding
        return binding.root
    }
}


