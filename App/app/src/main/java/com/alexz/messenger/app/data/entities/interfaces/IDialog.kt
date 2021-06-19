package com.alexz.messenger.app.data.entities.interfaces

interface IDialog : IMessageable,IUserContainer {
    var user1: String
    var user2: String
}