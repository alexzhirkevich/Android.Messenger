package com.alexz.messenger.app.util

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

fun <T : Any> QuerySnapshot.toList(clazz : Class<T>) : List<T> =
        documents.mapNotNull { it.toObject(clazz) }

fun <T> DocumentSnapshot.toObjectNonNull(clazz : Class<T>) : T =
        toObject(clazz) ?: throw ClassCastException("Parsing error")