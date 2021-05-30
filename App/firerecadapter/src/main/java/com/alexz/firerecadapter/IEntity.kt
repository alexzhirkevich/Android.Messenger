package com.alexz.firerecadapter

/**
 * Interface for base firebase database Entity
 * */
interface IEntity : Comparable<IEntity> {
    var id: String
}