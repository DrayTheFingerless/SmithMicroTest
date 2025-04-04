package com.ferreirarobert.smithmicrotest.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.data.position
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ferreirarobert.smithmicrotest.models.Note
import com.ferreirarobert.smithmicrotest.viewmodels.NotesViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun NotesScreen(
    //modifier,
    navController: NavController,
    notesVM: NotesViewModel = hiltViewModel()
) {

    LaunchedEffect(true)  {
        notesVM.getUsernameAndNotes()
    }

    val notes by notesVM.notes.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newNoteText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Notes",
                    fontSize = 24.sp,
                )
                Button(onClick = {
                    notesVM.wipeUsername()
                    navController.navigate(route = "login")
                }) {
                    Text("Logout")
                }
            }
        Box(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFFF8F0FF))
                        .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                ) {// List Below
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(notes) { note ->
                            NoteItem(
                                note = note,
                                onDelete = { notesVM.deleteNote(note) }
                            )
                        }
                    }
                }
                // Map View
                MapViewComposable(modifier = Modifier
                    .weight(0.5f),
                    notes = notes)
            }


            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = ButtonDefaults.buttonColors().containerColor,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add new Note"
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Note") },
            text = {
                Column {
                    TextField(
                        value = newNoteText,
                        onValueChange = { newNoteText = it },
                        label = { Text("Enter note content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Increased height
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    notesVM.postNote(newNoteText)
                    newNoteText = ""
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NoteItem(note: Note, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = note.content,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Note"
            )
        }
    }
}

@Composable
fun MapViewComposable(modifier: Modifier = Modifier, notes: List<Note> = emptyList()) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp) // Add padding around the map
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)) // Add a border
            .shadow(8.dp, RoundedCornerShape(16.dp)), // Add a shadow,
        factory = {
            org.osmdroid.config.Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                val mapController = controller
                mapController.setZoom(11.0)
                val startPoint = GeoPoint(37.7749, -122.4194)
                mapController.setCenter(startPoint)
            }
        },
        update = {
                mapView ->
            Log.d("MapViewComposable", "Updating MapView")
            mapView.overlays.removeAll { it is Marker } // Remove existing markers

            notes.forEach { note ->
                if (note.latitude != 0.0 && note.longitude != 0.0) {
                    val noteLocation = GeoPoint(note.latitude, note.longitude)
                    val marker = Marker(mapView)
                    marker.position = noteLocation
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = note.content
                    marker.snippet = "Note by ${note.user}"
                    mapView.overlays.add(marker)
                }
            }
            mapView.invalidate() // Refresh the map
        }
    )
}

@Preview
@Composable
fun NotesScreenPreview() {
    val mockNavCon = rememberNavController()
    NotesScreen(mockNavCon)
}