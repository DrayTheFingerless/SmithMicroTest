package com.ferreirarobert.smithmicrotest.models

data class Note(
    val id: String = "",
    val content: String = "",
    val user: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)