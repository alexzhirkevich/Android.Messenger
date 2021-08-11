package com.community.messenger.app.ui.viewmodels

import androidx.lifecycle.*
import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IDialog
import com.community.messenger.common.entities.interfaces.IGroup
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.components.DaggerChatsProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChatsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import kotlinx.coroutines.rx2.asFlow

class ChatDataViewModel(chat: IChat ) : DataViewModel<IChat>(), Parameterized<IChat> {

    class Factory(private val chat: IChat) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            ChatDataViewModel(chat) as T
    }

    lateinit var companion: LiveData<IUser>
        private set

    lateinit var members: LiveData<Int>
        private set

    override var parameter: IChat = chat
        set(value) {
            field = value
            update(value)
        }

    override fun onCleared() {
        super.onCleared()
        companionDisposable?.dispose()
    }

    private var companionDisposable: Disposable? = null


    private val usersProvider: UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    private val chatsProvider: ChatsProvider by lazy {
        DaggerChatsProviderComponent.create().getProvider()
    }

    private fun update(chat : IChat) {
        collect(chatObservable.asFlow())

        val companionId =  if (chat is IDialog) {
            if (usersProvider.currentUserId == chat.user1)
                chat.user1
            else
                chat.user2
        } else ""
        companion = createCompanionObservable(companionId)
            .asFlow().asLiveData(viewModelScope.coroutineContext)
        members = createMembersObservable(chat.id).map { it.size }
            .asFlow().asLiveData(viewModelScope.coroutineContext)
    }

    private val chatObservable: Observable<IChat>
        get() = chatsProvider.get(parameter.id)
            .onErrorResumeNext(Function { chatObservable })


    private fun createCompanionObservable(id : String) : Observable<IUser> =
        if (parameter is IDialog) {
            usersProvider.get(id)
                .onErrorResumeNext(Function {
                    Thread.sleep(TIMEOUT)
                    createCompanionObservable(id)
                })
        } else Observable.error(IllegalArgumentException("Cannot get companion from group. Parameter must implement IDialog"))

    private fun createMembersObservable(id : String) : Observable<Collection<IUser>> =
        if (parameter is IGroup) {
           chatsProvider.getUsers(id,limit = -1)
                .onErrorResumeNext(Function {
                    Thread.sleep(TIMEOUT)
                    createMembersObservable(id)
                })
        } else Observable.error(IllegalArgumentException("Cannot get members from dialog. Parameter must implement IGroup"))

    private companion object {
        private const val TIMEOUT = 1000L
    }

    init {
        update(chat)
    }
}