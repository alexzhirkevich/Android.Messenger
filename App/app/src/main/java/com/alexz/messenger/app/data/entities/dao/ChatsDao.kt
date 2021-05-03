package com.alexz.messenger.app.data.entities.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexz.messenger.app.data.entities.imp.Chat
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ChatsDao : EntityDao<Chat> {

    @Query("SELECT * FROM ${Chat.TABLE_NAME} WHERE id = (':id') LIMIT 1")
    override fun get(id: String): Single<Chat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun add(entity: Chat): Completable

    @Query("DELETE FROM ${Chat.TABLE_NAME} WHERE id = (':id')")
    override fun delete(id: String): Completable

    @Query("SELECT EXISTS(SELECT id from ${Chat.TABLE_NAME} WHERE id = (':id'))")
    override fun contains(id: String): Single<Boolean>

    @Query("SELECT * FROM ${Chat.TABLE_NAME}")
    fun getAll(): Single<List<Chat>>
}