package com.ferreirarobert.smithmicrotest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ferreirarobert.smithmicrotest.ui.theme.SmithMicroTestTheme
import com.ferreirarobert.smithmicrotest.views.LoginScreen
import com.ferreirarobert.smithmicrotest.views.NotesScreen
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SmithMicroTestTheme(dynamicColor = false) {
                MainScreen()
            }
        }
    }
}


@Composable
fun MainScreen(

) {
    Surface() {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            containerColor = Color(0xFFF0E6FF)) { innerPadding ->
            val navController = rememberNavController()

            NavHost(
                navController, "login",
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("login") {
                    LoginScreen(
                        navController
                    )
                }
                composable("notes") {
                    NotesScreen(
                        navController
                    )
                }
            }
        }
    }
}



