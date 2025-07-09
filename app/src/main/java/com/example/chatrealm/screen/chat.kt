package com.example.chatrealm.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatrealm.R
import com.example.chatrealm.ViewModel.MessageViewModel
import com.example.chatrealm.ViewModel.RoomViewModel
import com.example.chatrealm.data.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    roomId: String,
    messageViewModel: MessageViewModel = viewModel(),
    roomViewModel: RoomViewModel = viewModel()
    ) {
    val messages by messageViewModel.messages.observeAsState(emptyList())
    val currentUser by messageViewModel.currentUser.observeAsState()
    messageViewModel.setRoomId(roomId)

    val roomName by roomViewModel.roomName.observeAsState("Loading...")
    // Load the room name when this screen is shown
    roomViewModel.loadRoomName(roomId)

    val text = remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, bottom = 16.dp, end = 16.dp, top = 4.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.teal_200)
            )
        ) {
            Text(
                text = roomName,
                fontSize = 20.sp,
                color = colorResource(id = R.color.grey),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the chat messages
        if (currentUser != null) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    ChatMessageItem(
                        message = message.copy(
                            isSentByCurrentUser = message.senderId == currentUser?.email
                        )
                    )
                }
            }
        }


        // Chat input field and send icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.weight(1f)
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(12.dp) // 👈 corner radius here
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)

            ) {
                BasicTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 22.sp),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)

                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    // Send the message when the icon is clicked
                    if (text.value.isNotEmpty()) {
                        messageViewModel.sendMessage(text.value.trim())
                        text.value = ""
                    }
                    messageViewModel.loadMessages()
                },
                modifier = Modifier.background(color = colorResource(R.color.teal_200))
            ){
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()

    return when {
        isSameDay(messageDateTime, now) -> "today ${formatTime(messageDateTime)}"
        isSameDay(messageDateTime.plusDays(1), now) -> "yesterday ${formatTime(messageDateTime)}"
        else -> formatDate(messageDateTime)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
        Text(
            text = formatTimestamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}