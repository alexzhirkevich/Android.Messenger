package com.alexz.messenger.app.data.entities.dao

import androidx.room.*
import com.alexz.messenger.app.data.entities.imp.Post
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface PostsDao : EntityDao<Post> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun add(entity: Post) : Completable

    @Query("SELECT EXISTS(SELECT id from ${Post.TABLE_NAME} WHERE id = (':id'))")
    override fun contains(id: String): Single<Boolean>

    @Delete
    override fun delete(id: String) : Completable

    @Query("SELECT * FROM ${Post.TABLE_NAME} WHERE channelId = (':channelId') ORDER BY time")
    fun getAll(channelId : String) : Single<List<Post>>

    @Query("SELECT * FROM ${Post.TABLE_NAME} WHERE channelId = (':channelId') ORDER BY time LIMIT 1")
    fun lastPost(channelId: String): Single<Post>
}