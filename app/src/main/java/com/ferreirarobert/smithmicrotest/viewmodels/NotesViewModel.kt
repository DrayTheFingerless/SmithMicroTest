package com.ferreirarobert.smithmicrotest.viewmodels

import android.util.Log
import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferreirarobert.smithmicrotest.api.FirestoreRepository
import com.ferreirarobert.smithmicrotest.models.Note
import com.ferreirarobert.smithmicrotest.models.User
import com.ferreirarobert.smithmicrotest.repositories.UserDataStore
import com.google.android.gms.tasks.Tasks.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    var username: String = ""
    fun wipeUsername() {
        viewModelScope.launch {
            userDataStore.saveUsername("")
        }
    }

    fun getUsernameAndNotes() {
       viewModelScope.launch {
           username = runBlocking { userDataStore.getUsername().first() }
           fetchNotes(username)
       }
    }

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _postSuccess = MutableStateFlow<Boolean?>(null)
    val postSuccess: StateFlow<Boolean?> = _postSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser


    fun fetchNotes(userId: String) {
        viewModelScope.launch {
            repository.getNotesForUser(
                username = userId,
                onNotesReady = { // success
                    fetchedNotes ->
                    // Add random geolocation to each note
                    val notesWithGeolocation = fetchedNotes.map { note ->
                        note.copy(
                            latitude = getRandomLatitude(),
                            longitude = getRandomLongitude()
                        )
                    }
                    _notes.value = notesWithGeolocation
                },
                onError = { // error
                    message, exception ->
                    _postSuccess.value = false
                    _errorMessage.value = message
                    Log.e(TAG, message, exception)
                })
        }
    }

    fun postNote(note: String) {
        var newNote = Note(
            content = note,
            user = username,
            latitude = getRandomLatitude(),
            longitude = getRandomLongitude()
        )
        viewModelScope.launch {
            repository.postNote(
                note = newNote,
                onNotePosted = { postedNote ->
                    _postSuccess.value = true
                    _errorMessage.value = null
                    //update list of notes after adding new one
                    val newList = _notes.value.toMutableList() + postedNote
                    _notes.value = newList.toList()
                },
                onError = {
                  message, exception ->
                    Log.e(TAG, message, exception)
                    _postSuccess.value = false
                    _errorMessage.value = message
                }
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(
                note = note,
                onNoteDeleted = {
                    val updatedNotes = _notes.value.toMutableList()
                    updatedNotes.remove(note)
                    _notes.value = updatedNotes
                },
                onError = { message, exception ->
                    Log.e(TAG, message, exception)
                }
            )
        }
    }

    // Function to generate a random latitude around a center
    private fun getRandomLatitude(): Double {
        val centerLatitude = 37.7749
        val randomOffset = Random.nextDouble(-0.1, 0.1) // Adjust range as needed
        return centerLatitude + randomOffset
    }

    // Function to generate a random longitude around a center
    private fun getRandomLongitude(): Double {
        val centerLongitude = -122.4194 // Example: Los Angeles
        val randomOffset = Random.nextDouble(-0.1, 0.1) // Adjust range as needed
        return centerLongitude + randomOffset
    }

    fun resetError() {
        _errorMessage.value = null
    }

    companion object {
        private const val TAG = "NOTES"
    }
}