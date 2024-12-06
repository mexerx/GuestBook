package com.example.aufgabe3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aufgabe3.model.BookingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

class SharedViewModel: ViewModel() {
    private val _bookingsEntries = MutableStateFlow<List<BookingEntry>>(emptyList())
    val bookingsEntries: StateFlow<List<BookingEntry>> = _bookingsEntries.asStateFlow()

    private val nextBookingId = AtomicInteger(0)
    fun addBookingEntry(bookingEntry: BookingEntry) {
        val newBookingEntry = bookingEntry.copy(id = nextBookingId.incrementAndGet())
        _bookingsEntries.update { currentList ->
            currentList + newBookingEntry
        }
    }

    fun deleteBookingEntry(bookingEntry: BookingEntry) {
        _bookingsEntries.update { currentList ->
            currentList.filterNot { it.id == bookingEntry.id }
        }
    }
}