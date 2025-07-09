package com.example.chatrealm.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatrealm.R
import com.example.chatrealm.ViewModel.RoomViewModel
import com.example.chatrealm.data.Room

@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    onJoinClicked: (Room) -> Unit,
    onBotClicked: () -> Unit
){
    val rooms by roomViewModel.rooms.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }


    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.back_chat_room),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Chat Rooms", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(rooms) { room ->
                    RoomItem(room = room, onJoinClicked = { onJoinClicked(room) })
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Room")
            }
        }

        // FAB for Chatbot
        FloatingActionButton(
            onClick = { onBotClicked() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.White  // Optional: for better contrast
        ) {
            Icon(
                painter = painterResource(id = R.drawable.chatbot),
                contentDescription = "Chatbot",
                modifier = Modifier.size(71.dp),  // Match your icon's ideal size
                tint = Color.Unspecified  // Don't apply tint to actual image
            )
        }

    }


    if (showDialog){
        AlertDialog( onDismissRequest = { showDialog = true },
            title = { Text("Create a new room") },
            text={
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }, confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {

                                roomViewModel.createRoom(name)
                                name = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            })
    }

}

@Composable
fun RoomItem(room: Room, onJoinClicked:(Room)->Unit, roomViewModel:RoomViewModel = viewModel()) {

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = room.name, fontSize = 16.sp, fontWeight = FontWeight.Normal)

            Row {
                OutlinedButton(onClick = { onJoinClicked(room) }) {
                    Text("Join")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                IconButton(onClick = { roomViewModel.deleteRoom(room.id) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Room")
                }
            }
        }

    }

}
