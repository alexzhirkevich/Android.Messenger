package com.alexz.messenger.app.ui.viewmodels

import com.alexz.messenger.app.data.entities.imp.Event
import com.alexz.messenger.app.data.entities.interfaces.IEvent
import io.reactivex.Observable

class EventsViewModel : DataViewModel<List<IEvent>>(), Updatable {

    override fun update() {
        observe(Observable.just(listOf(
                Event(name = "A event", time = System.currentTimeMillis() + 1000000000, address = "Adress",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                                "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                                "culpa qui officia deserunt mollit anim id est laborum",isValid = false),
                Event(name = "B event", time = System.currentTimeMillis() + 100000000, creatorId = "t4lqbcXb79Z3fOjfo5yMf5lyG3f2", address = "Adress",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                                "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                                "culpa qui officia deserunt mollit anim id est laborum"),
                Event(name = "C event", time = System.currentTimeMillis(), isEnded = true, address = "Adress",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                                "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                                "culpa qui officia deserunt mollit anim id est laborum")
        )))
    }
}