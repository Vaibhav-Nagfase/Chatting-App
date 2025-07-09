package com.example.chatrealm.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatBotRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getBotChats(userId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("bot-chats")
            .whereEqualTo("userId", userId)
            .orderBy("createTime")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messageList = mutableListOf<Message>()

                snapshot?.documents?.forEach { doc ->
                    val prompt = doc.getString("prompt") ?: return@forEach
                    val response = doc.getString("response")
                    val time = doc.getLong("createTime") ?: System.currentTimeMillis()

                    // Always add user prompt
                    messageList.add(
                        Message(
                            senderFirstName = "You",
                            senderId = userId,
                            text = prompt,
                            timestamp = time,
                            isSentByCurrentUser = true
                        )
                    )

                    // If response is available, add it too
                    if (!response.isNullOrBlank()) {
                        messageList.add(
                            Message(
                                senderFirstName = "Bot",
                                senderId = "bot",
                                text = response,
                                timestamp = time + 1,
                                isSentByCurrentUser = false
                            )
                        )
                    }
                }

                trySend(messageList.sortedBy { it.timestamp })
            }

        awaitClose { listener.remove() }
    }



    fun sendPrompt(prompt: String, userId: String) {
        val data = hashMapOf(
            "prompt" to prompt,
            "createTime" to System.currentTimeMillis(),
            "userId" to userId
        )
        firestore.collection("bot-chats").add(data)
    }
}
