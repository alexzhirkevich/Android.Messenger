package com.alexz.messenger.app.data.model.interfaces

interface IMediaContent {
    companion object {
        const val IMAGE = 0
        const val VIDEO = 1
    }

    var type: Int
    var url: String
}