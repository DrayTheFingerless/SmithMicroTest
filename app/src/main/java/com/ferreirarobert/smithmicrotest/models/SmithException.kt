package com.ferreirarobert.smithmicrotest.models

//TODO insert custom enum for exceptions
enum class NetworkError {
    USER_ALREADY_EXIST,
    USER_INVALID,
    PASSWORD_INVALID,
    USER_PASSWORD_INVALID,
    NOTE_NOT_CREATED,
    NOTE_INCORRECT_FORMAT,
    NO_INTERNET,
    UNKNOWN;

    companion object {
        fun returnMessage(netError: NetworkError): String {
            return when (netError) {
                USER_ALREADY_EXIST -> "User already exists"
                USER_INVALID -> "User is invalid"
                PASSWORD_INVALID -> "Password is invalid"
                USER_PASSWORD_INVALID -> "User or password are invalid"
                NOTE_NOT_CREATED -> "Note was not created"
                NOTE_INCORRECT_FORMAT -> "Note has incorrect formatting"
                NO_INTERNET -> "Error connecting to server"
                UNKNOWN -> "Unknown error"
            }
        }
    }
}