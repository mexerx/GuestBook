package com.example.aufgabe3.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class BookingEntry(
    val id: Int ,
    val name: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val color: Color

)
