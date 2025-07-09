package com.example.chatrealm.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoomRepository(private val firestore: FirebaseFirestore) {

    suspend fun createRoom(name: String): Result<Unit> = try {
        val room = Room(name = name)
        firestore.collection("rooms").add(room).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getRooms(): Result<List<Room>> = try {
        val querySnapshot = firestore.collection("rooms").get().await()
        val rooms = querySnapshot.documents.map { document ->
            document.toObject(Room::class.java)!!.copy(id = document.id)
        }
        Result.Success(rooms)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getRoomById(roomId: String): Result<Room> = try {
        val document = firestore.collection("rooms").document(roomId).get().await()
        val room = document.toObject(Room::class.java)?.copy(id = document.id)
        if (room != null) {
            Result.Success(room)
        } else {
            Result.Error(Exception("Room not found"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }


    suspend fun deleteRoom(roomId: String): Result<Unit> = try {
        firestore.collection("rooms").document(roomId).delete().await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

}