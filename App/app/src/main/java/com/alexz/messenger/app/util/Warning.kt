package com.alexz.messenger.app.util

data class Warning(val str: String= "") : Throwable(str)