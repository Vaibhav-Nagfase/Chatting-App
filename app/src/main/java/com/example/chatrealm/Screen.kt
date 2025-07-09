package com.example.chatrealm

sealed class Screen(val route:String) {

    object LoginScreen:Screen("loginscreen")
    object SignUpScreen:Screen("signupscreen")
    object ChatRoomsScreen:Screen("chatroomscreen")
    object ChatScreen:Screen("chatscreen")
    object ChatBotScreen:Screen("chatbot")
}