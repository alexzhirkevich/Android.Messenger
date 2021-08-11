package com.community.messenger.app.ui.fragments

import com.community.messenger.common.entities.interfaces.IEvent


class PersonalEventsFragment : EventsPagerFragment() {

    override fun onFilterEvents(events: Collection<IEvent>): Collection<IEvent> {
        return events.filter { it.creatorId == viewModel.currentUserId }
    }
}