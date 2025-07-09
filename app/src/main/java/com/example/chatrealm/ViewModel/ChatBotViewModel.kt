package com.example.chatrealm.ViewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatrealm.data.ChatBotRepository
import com.example.chatrealm.data.Message

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ChatBotViewModel(
    private val repository: ChatBotRepository = ChatBotRepository()
) : ViewModel() {

    private val _botMessages = MutableLiveData<List<Message>>()
    val botMessages: LiveData<List<Message>> = _botMessages

    private val auth = FirebaseAuth.getInstance()

    init {
        viewModelScope.launch {
            // Wait until Firebase is fully ready
            while (auth.currentUser?.uid == null) {
                delay(100)
            }

            val uid = auth.currentUser?.uid ?: return@launch
            listenToBotChats(uid)
        }
    }


    private fun listenToBotChats(uid: String) {
        viewModelScope.launch {
            repository.getBotChats(uid).collect { messages ->
                _botMessages.value = messages

                Log.d("ChatBotViewModel", "Collected ${messages.size} messages")
                messages.forEach {
                    Log.d("ChatBotMessage", "Text: ${it.text} | Sender: ${it.senderId}")
                }

            }
        }
    }

    fun sendBotPrompt(prompt: String) {
        val uid = auth.currentUser?.uid ?: return
        repository.sendPrompt(prompt, uid)
    }
}
