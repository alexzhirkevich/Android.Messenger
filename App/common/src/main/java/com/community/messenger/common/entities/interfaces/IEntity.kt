package com.community.messenger.common.entities.interfaces

/**
 * Interface for base firebase database Entity
 * */
interface IEntity : Comparable<IEntity> {
    var id: String
}