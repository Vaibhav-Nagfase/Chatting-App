package com.example.chatrealm.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatrealm.R
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import com.example.chatrealm.ViewModel.ChatBotViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatBotScreen(
    viewModel: ChatBotViewModel = viewModel()
) {
    val messages by viewModel.botMessages.observeAsState(emptyList())
    val userInput = remember { mutableStateOf("") }
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = colorResource(id = R.color.chat_input_background),
                    shape = RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ask me anything 💬",
                fontSize = 24.sp,
                color = Color.Black,
                style = MaterialTheme.typography.headlineSmall
            )
        }


        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                val adjustedMessage = message.copy(
                    isSentByCurrentUser = message.senderId == currentUid
                )
                ChatMessageItem(message = adjustedMessage)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.chat_input_background)
                )
            ) {
                BasicTextField(
                    value = userInput.value,
                    onValueChange = { userInput.value = it },
                    textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (userInput.value.isEmpty()) {
                            Text(
                                "Type a message...",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (userInput.value.isNotEmpty()) {
                        viewModel.sendBotPrompt(userInput.value.trim())
                        userInput.value = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = colorResource(id = R.color.send_button_color),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }

        }
    }
}
