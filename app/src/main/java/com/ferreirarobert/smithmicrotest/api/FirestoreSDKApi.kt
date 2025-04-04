package com.ferreirarobert.smithmicrotest.api
import android.content.ContentValues.TAG
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.text2.input.delete
import androidx.compose.ui.geometry.isEmpty
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
                        onError: (String, Exception) -> Unit
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
            onError("Error getting users.", exception)
        }

    }

    fun postNote(
        note: Note,
        onNotePosted: (Note) -> Unit,
        onError: (String, Exception) -> Unit
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
                                onError("Error converting document to Note object.", Exception("Conversion failed"))
                            }
                        } else {
                            onError("Error getting newly created note.", Exception("Document does not exist"))
                        }
                    }
                    .addOnFailureListener { exception ->
                        onError("Error getting newly created note.", exception)
                    }
            }
            .addOnFailureListener { exception ->
                onError("Error adding note.", exception)
            }
    }

    fun deleteNote(
        note: Note,
        onNoteDeleted: () -> Unit,
        onError: (String, Exception) -> Unit
    ) {
        notesCollection.document(note.id).delete()
            .addOnSuccessListener {
                Log.d(TAG, "Note deleted!")
                onNoteDeleted()
            }
            .addOnFailureListener { exception ->
                onError("Error deleting note.", exception)
            }
    }

    fun getUser(username: String,
                password: String,
                onUserReady: (User) -> Unit,
                onError: (String, Exception) -> Unit) {

        usersCollection
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onError("User not found", Exception("User not found"))
                    return@addOnSuccessListener
                } else {
                    val document = result.first()
                    val user = document.toObject(User::class.java)
                    Log.d(TAG, "${document.id} => ${document.data}")
                    onUserReady(user)
                }
            }
            .addOnFailureListener { exception ->
                onError("Error logging in", exception)
            }
    }

    fun postUser(
        user: User,
        onUserPosted: (String) -> Unit,
        onError: (String, Exception) -> Unit
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
                            onError("Error adding user.", exception)
                        }
                } else {
                    // A user with the same username already exists
                    onError("Username already exists.", Exception("Username already exists"))
                }
            }
            .addOnFailureListener { exception ->
                onError("Error checking for existing username.", exception)
            }
    }

    private val TAG = "Firestore"
}