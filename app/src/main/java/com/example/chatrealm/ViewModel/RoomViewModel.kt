package com.example.chatrealm.ViewModel


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatrealm.Injection
import com.example.chatrealm.data.Result
import com.example.chatrealm.data.Result.*
import com.example.chatrealm.data.RoomRepository
import kotlinx.coroutines.launch
import com.example.chatrealm.data.Room

class RoomViewModel : ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    private val _roomName = MutableLiveData<String>()
    val roomName: LiveData<String> get() = _roomName


    private val roomRepository: RoomRepository
    init {
        roomRepository = RoomRepository(Injection.instance())
        loadRooms()
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            when (roomRepository.createRoom(name)) {
                is Success -> loadRooms()
                is Error -> {

                }
            }
        }
    }

    fun loadRooms() {
        viewModelScope.launch {
            when (val result = roomRepository.getRooms()) {
                is Success -> _rooms.value = result.data
                is Error -> {

                }
            }
        }
    }

    fun loadRoomName(roomId: String) {
        viewModelScope.launch {
            when (val result = roomRepository.getRoomById(roomId)) {
                is Result.Success -> _roomName.value = result.data.name
                is Result.Error -> _roomName.value = "Unknown Room"
            }
        }
    }


    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            when (roomRepository.deleteRoom(roomId)) {
                is Success -> {
                    // Reload the rooms list
                    loadRooms()
                }
                is Error -> {
                    // Optionally log or show an error message
                }
            }
        }
    }

}