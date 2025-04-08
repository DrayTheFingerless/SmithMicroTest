package com.ferreirarobert.smithmicrotest.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ferreirarobert.smithmicrotest.viewmodels.MainViewModel
import com.ferreirarobert.smithmicrotest.viewmodels.UserViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Composable
fun LoginScreen(
    //modifier,
    navController: NavController,
    mainVM: MainViewModel,
    userVM: UserViewModel = hiltViewModel()
){

    var usernameState by remember { mutableStateOf("robert") }
    var passwordState by remember { mutableStateOf("1234") }
    var loggedUser = userVM.user.collectAsState()
    var showRegister = userVM.showRegister.collectAsState()
    var showError = userVM.errorMessage.collectAsState()

    showError.value?.let {
        val errorMessage = it
        mainVM.showErrorMessage(errorMessage)
        userVM.resetError()
    }

    //checks if user is logged in, or exists, if so, shows the notes, otherwise shows loginscreen
    if(loggedUser.value != null) {
        navController.navigate(
            route = "notes"
        )
        userVM.resetLoginSuccess()
    } else {

        if (showRegister.value)
        //register u
            RegisterScreen(
                userVM
            )
        else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFAA90AB)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                //login ui
                Column(
                    modifier = Modifier
                        .padding(16.dp), // Add padding around the Column
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    SimpleTextField(
                        "Username",
                        usernameState
                    ) { newValue ->  // pass callback function to child Composable
                        usernameState =
                            newValue    // set updated value received from child Composable
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SimpleTextField(
                        label = "Password",
                        password = true,
                        textState = passwordState
                    ) { newValue ->  // pass callback function to child Composable
                        passwordState =
                            newValue    // set updated value received from child Composable
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            userVM.getUser(usernameState, passwordState)
                        },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text("Login")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = {
                        userVM.setShowRegister(true)
                    }) {
                        Text(
                            text = "Register",
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    userVM: UserViewModel
) {
    var usernameState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFAA90AB)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleTextField(
                label = "Username",
                usernameState
            ) { newValue ->  // pass callback function to child Composable
                usernameState = newValue    // set updated value received from child Composable
            }
            Spacer(modifier = Modifier.height(8.dp)) // Add spacing between elements

            SimpleTextField(
                label = "Password",
                textState = passwordState,
                password = true
            ) { newValue ->  // pass callback function to child Composable
                passwordState = newValue    // set updated value received from child Composable
            }
            Spacer(modifier = Modifier.height(16.dp)) // Add spacing between elements

            Button( modifier = Modifier.width(200.dp),
                    onClick = {
                userVM.postUser(usernameState, passwordState)
            }) {
                Text("Register")
            }
            TextButton(onClick = {
                userVM.setShowRegister(false)
            }) {
                Text("Cancel")
            }
        }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    val mockNavCon = rememberNavController()
    LoginScreen(mockNavCon, MainViewModel())
}