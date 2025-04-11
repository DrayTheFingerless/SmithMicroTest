package com.ferreirarobert.smithmicrotest.api
import android.content.ContentValues.TAG
import android.net.Network
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.text2.input.delete
import androidx.compose.ui.geometry.isEmpty
import com.ferreirarobert.smithmicrotest.models.NetworkError
import com.ferreirarobert.smithmicrotest.models.Note
import com.ferreirarobert.smithmicrotest.models.User
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val notesCollection = db.collection("notes")
    private val usersCollection = db.collection("users")

    fun getNotesForUser(username: String,
                        onNotesReady: (List<Note>) -> Unit,
                        onError: (NetworkError, Exception?) -> Unit
    ) {
        notesCollection
        .whereEqualTo("user", username)
        .get()
        .addOnSuccessListener { result ->
            val notes = mutableListOf<Note>()
            for (document in result) {
                val note = document.toObject(Note::class.java)
                notes.add(note.copy(id = document.id))
            }
            onNotesReady(notes)
        }
        .addOnFailureListener { exception ->
            onError(NetworkError.NO_INTERNET, exception)
        }

    }

    fun postNote(
        note: Note,
        onNotePosted: (Note) -> Unit,
        onError: (NetworkError, Exception?) -> Unit
    ) {
        val newNote = hashMapOf(
            "content" to note.content,
            "user" to note.user,
            "latitude" to note.latitude,
            "longitude" to note.longitude
        )

        notesCollection.add(newNote)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                // Fetch the newly created note with the generated ID
                documentReference.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val createdNote = documentSnapshot.toObject(Note::class.java)
                            if (createdNote != null) {
                                onNotePosted(createdNote.copy(id = documentSnapshot.id))
                            } else {
                                onError(NetworkError.NOTE_INCORRECT_FORMAT, null)
                            }
                        } else {
                            onError(NetworkError.NOTE_NOT_CREATED, null)
                        }
                    }
                    .addOnFailureListener { exception ->
                        onError(NetworkError.NO_INTERNET, exception)
                    }
            }
            .addOnFailureListener { exception ->
                onError(NetworkError.NO_INTERNET, exception)
            }
    }

    fun deleteNote(
        note: Note,
        onNoteDeleted: () -> Unit,
        onError: (NetworkError, Exception?) -> Unit
    ) {
        notesCollection.document(note.id).delete()
            .addOnSuccessListener {
                Log.d(TAG, "Note deleted!")
                onNoteDeleted()
            }
            .addOnFailureListener { exception ->
                onError(NetworkError.NO_INTERNET, exception)
            }
    }

    fun getUser(username: String,
                password: String,
                onUserReady: (User) -> Unit,
                onError: (NetworkError, Exception?) -> Unit) {

        usersCollection
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError(NetworkError.USER_PASSWORD_INVALID, null)
                    return@addOnSuccessListener
                } else {
                    val document = result.first()
                    val user = document.toObject(User::class.java)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    onUserReady(user)
                }
            }
            .addOnFailureListener { exception ->
                onError(NetworkError.NO_INTERNET, exception)
            }
    }

    fun postUser(
        user: User,
        onUserPosted: (String) -> Unit,
        onError: (NetworkError, Exception?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        // Check if a user with the same username already exists
        usersCollection
            .whereEqualTo("username", user.username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No user with the same username exists, proceed with adding the new user
                    val newUser = hashMapOf(
                        "username" to user.username,
                        "password" to user.password,
                    )

                    usersCollection.add(newUser)
                        .addOnSuccessListener { documentReference ->
                            onUserPosted("${user.username} created")
                        }
                        .addOnFailureListener { exception ->
                            onError(NetworkError.NO_INTERNET, exception)
                        }
                } else {
                    // A user with the same username already exists
                    onError(NetworkError.USER_ALREADY_EXIST, null)
                }
            }
            .addOnFailureListener { exception ->
                onError(NetworkError.NO_INTERNET, exception)
            }
    }

    private val TAG = "Firestore"
}