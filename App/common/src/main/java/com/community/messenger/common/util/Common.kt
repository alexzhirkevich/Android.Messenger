package com.community.messenger.common.util



class SingleAction  {
    var isEmitted : Boolean = false
        private set

    fun doSingle(action : () -> Unit) {
        if (!isEmitted) {
            isEmitted = true
            action()
        }
    }

}