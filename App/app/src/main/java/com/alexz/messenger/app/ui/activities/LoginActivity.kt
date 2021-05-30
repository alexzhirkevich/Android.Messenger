//package com.alexz.messenger.app.ui.activities
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import com.alexz.messenger.app.data.repo.UiHandler
//import com.alexz.messenger.app.ui.viewmodels.AuthViewModel
//import com.alexz.messenger.app.util.FirebaseUtil
//import com.alexz.messenger.app.util.MetrixUtil
//import com.alexz.messenger.app.util.MyGoogleUtils
//import com.alexz.messenger.app.util.VibrateUtil
//import com.google.android.gms.common.GoogleApiAvailability
//import com.messenger.app.R
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.activity_login.*
//
//class LoginActivity : AppCompatActivity(), View.OnClickListener {
//
//    private val viewModel : AuthViewModel by viewModels()
//    private var counter = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //overridePendingTransition(R.anim.anim_alpha_in,R.anim.anim_alpha_out)
//        setContentView(R.layout.activity_login)
//        btnGoogleSignIn.apply {
//            setOnClickListener(View.OnClickListener { view: View? -> onGoogleSignIn(view) })
//            visibility = View.INVISIBLE
//        }
//        button1.visibility = View.INVISIBLE
//        button2.visibility = View.INVISIBLE
//        question.visibility = View.INVISIBLE
//        logo.setOnClickListener(this)
//        val easter = View.OnClickListener {
//            val view1 = LayoutInflater.from(this).inflate(R.layout.easter, logo, false)
//            val t = Toast(this)
//            t.view = view1
//            t.duration = Toast.LENGTH_LONG
//            t.show()
//            question.visibility = View.INVISIBLE
//            button1.visibility = View.INVISIBLE
//            button2.visibility = View.INVISIBLE
//            logo.animate().scaleX(1f).scaleY(1f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationStart(animation: Animator) {
//                    super.onAnimationEnd(animation)
//                    logo.visibility = View.VISIBLE
//                }
//            }).start()
//            VibrateUtil.with(this).vibrate(100, VibrateUtil.POWER_LOW)
//        }
//        button1.setOnClickListener(easter)
//        button2.setOnClickListener(easter)
//    }
//
//    private fun updateUI() {
//        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
//            UiHandler.postDelayed({ MainActivity.startActivity(this, intent.data) }, 200)
//        } else {
//            UiHandler.postDelayed({ MainActivity.startActivity(this) }, 200)
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val user = FirebaseUtil.currentFireUser
//        if (user != null) {
//            updateUI()
////            AuthRepository.updateUserInfo()
//        } else {
//            btnGoogleSignIn.visibility = View.VISIBLE
//        }
//    }
//
//    private fun onGoogleSignIn(view: View?) {
//        try {
//            val v = packageManager.getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode
//            if (v < 12451000) {
//                Toast.makeText(this, getString(R.string.error_gservices_ver) + v, Toast.LENGTH_LONG).show()
//                return
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//            return
//        }
//        startActivityForResult(MyGoogleUtils.getGoogleSignInClient(this).signInIntent, MyGoogleUtils.REQ_SIGN_IN)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val errToast = Toast.makeText(this, getString(R.string.error_google_login), Toast.LENGTH_LONG)
//        when (requestCode) {
//            AuthViewModel.REQ_SIGN_IN -> if (resultCode == Activity.RESULT_OK && data != null) {
//                viewModel.getGoogleSignInAccount(data)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                            viewModel.login(it)
//                                    .subscribeOn(Schedulers.newThread())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(
//                                            {
//                                                btnGoogleSignIn.visibility = View.GONE
//                                                updateUI()
//                                            },
//                                            {
//                                                errToast.show()
//                                            }
//                                    )
//                        }, { errToast.show() })
//
//            } else
//                errToast.show()
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(STR_COUNTER, counter)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        counter = savedInstanceState.getInt(STR_COUNTER)
//    }
//
//    override fun onClick(view: View) {
//        when (counter) {
//            2 -> {
//                Toast.makeText(this, R.string.easter_1, Toast.LENGTH_LONG).show()
//                counter++
//            }
//            10 -> {
//                Toast.makeText(this, R.string.easter_2, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            20 -> {
//                Toast.makeText(this, R.string.easter_3, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            30 -> {
//                Toast.makeText(this, R.string.easter_4, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            50 -> {
//                logo.animate().translationYBy(MetrixUtil.dpToPx(this, 300).toFloat()).setDuration(300).start()
//                Toast.makeText(this, R.string.easter_5, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            60 -> {
//                logo.animate().translationYBy(MetrixUtil.dpToPx(this, -300).toFloat()).setDuration(300).start()
//                Toast.makeText(this, R.string.easter_6, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            70 -> {
//                logo.animate().scaleX(0.1f).scaleY(0.1f).setDuration(300).start()
//                Toast.makeText(this, R.string.easter_7, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            71, 79 -> {
//                logo.animate().translationXBy(150f).setDuration(300).start()
//                counter++
//            }
//            73, 75, 77 -> {
//                logo.animate().translationXBy(300f).setDuration(300).start()
//                counter++
//            }
//            72, 74, 76, 78 -> {
//                logo.animate().translationXBy(-300f).setDuration(300).start()
//                counter++
//            }
//            80 -> {
//                Toast.makeText(this, R.string.easter_8, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            90 -> {
//                logo.animate().scaleX(2f).scaleY(2f).setDuration(100).setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        super.onAnimationEnd(animation)
//                        logo.animate().scaleX(1f).scaleY(1f).setDuration(200).setStartDelay(500).start()
//                    }
//                }).start()
//                VibrateUtil.with(this).vibrate(300, VibrateUtil.POWER_HIGH)
//                Toast.makeText(this, R.string.easter_9, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            100 -> {
//                val v = VibrateUtil.with(this).vibrate(150, VibrateUtil.POWER_MEDUIM)
//                logo.postDelayed(Runnable { v.vibrate(560, VibrateUtil.POWER_MEDUIM) }, 200)
//                Toast.makeText(this, R.string.easter_10, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            110 -> {
//                Toast.makeText(this, R.string.easter_11, Toast.LENGTH_SHORT).show()
//                counter++
//            }
//            120 -> {
//                counter++
//                question.setText(R.string.easter_kitties)
//                button1.setText(R.string.yes)
//                button2.setText(R.string.easter_yes)
//                question.visibility = View.VISIBLE
//                button1.visibility = View.VISIBLE
//                button2.visibility = View.VISIBLE
//                VibrateUtil.with(this).vibrate(50, VibrateUtil.POWER_LOW)
//                logo.animate().scaleX(0f).scaleY(0f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        super.onAnimationEnd(animation)
//                        logo.visibility = View.INVISIBLE
//                    }
//                }).start()
//            }
//            121, 122, 123, 124, 125, 126, 127, 128, 129 -> {
//                VibrateUtil.with(this).vibrate(counter % 10 * 10, VibrateUtil.POWER_LOW)
//                counter++
//            }
//            130 -> {
//                logo.animate().rotationX(360f).setDuration(1000).start()
//                VibrateUtil.with(this).vibrate(1000, VibrateUtil.POWER_HIGH)
//                counter++
//            }
//            150 -> {
//                Toast.makeText(this, R.string.easter_14, Toast.LENGTH_LONG).show()
//                val vi = VibrateUtil.with(this)
//                Thread(Runnable {
//                    try {
//                        var i = 0
//                        while (i < 4) {
//                            vi.vibrate(100, VibrateUtil.POWER_LOW)
//                            Thread.sleep(150)
//                            i++
//                        }
//                        Thread.sleep(150)
//                        vi.vibrate(150, VibrateUtil.POWER_LOW)
//                        Thread.sleep(170)
//                        vi.vibrate(1000)
//                    } catch (ignore: InterruptedException) {
//                    }
//                }).start()
//                counter++
//            }
//            160 -> {
//                Toast.makeText(this, R.string.easter_15, Toast.LENGTH_LONG).show()
//                logo.setOnClickListener { view: View? -> onGoogleSignIn(view) }
//                counter++
//            }
//            else -> counter++
//        }
//    }
//
//    companion object {
//        private const val STR_COUNTER = "counter"
//    }
//}