package com.alexz.messenger.app.data.repo

import android.os.Handler
import android.os.Looper

val UiHandler = Handler(Looper.getMainLooper())

val GsonInstance = com.google.gson.Gson()