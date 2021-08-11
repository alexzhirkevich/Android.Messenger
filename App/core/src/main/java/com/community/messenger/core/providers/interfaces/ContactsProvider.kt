package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.interfaces.IContact
import com.community.messenger.core.providers.base.RangeEntityProvider
import com.community.messenger.core.providers.base.SingleEntityProvider
import io.reactivex.disposables.Disposable

interface ContactsProvider :
    SingleEntityProvider<IContact>,
    RangeEntityProvider<IContact>,
    Disposable{

    var isSynchronizationEnabled : Boolean

   // fun synchronize(contacts : Collection<IContact>) : Completable
}