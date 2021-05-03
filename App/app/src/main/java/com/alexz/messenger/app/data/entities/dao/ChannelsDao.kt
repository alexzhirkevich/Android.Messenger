package com.alexz.messenger.app.data.entities.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexz.messenger.app.data.entities.imp.Channel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ChannelsDao : EntityDao<Channel> {

    @Query("SELECT * FROM ${Channel.TABLE_NAME} WHERE id = (':id') LIMIT 1")
    override fun get(id: String): Single<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun add(entity: Channel): Completable

    @Query("DELETE FROM ${Channel.TABLE_NAME} WHERE id = (':id')")
    override fun delete(id: String): Completable

    @Query("SELECT EXISTS(SELECT id from ${Channel.TABLE_NAME} WHERE id = (':id'))")
    override fun contains(id: String): Single<Boolean>

    @Query("SELECT * FROM ${Channel.TABLE_NAME}")
    fun getAll(): Single<List<Channel>>
}