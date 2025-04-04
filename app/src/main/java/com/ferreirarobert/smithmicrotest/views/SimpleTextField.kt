package com.ferreirarobert.smithmicrotest.views

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SimpleTextField(label: String,
                    textState: String,
                    password: Boolean = false,
                    onTextChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(!password) }

    TextField(
        value = textState,
        onValueChange = { onTextChange(it) },
        label = { Text(label)
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = if (!password) KeyboardOptions(keyboardType = KeyboardType.Password)
        else KeyboardOptions.Default,
    )
}