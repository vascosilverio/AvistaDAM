package com.example.avista.model

data class Observacao(
    val username: String,
    val photoBase64: String,
    val date: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val species: String
)