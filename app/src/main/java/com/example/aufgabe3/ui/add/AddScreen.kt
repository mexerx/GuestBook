package com.example.aufgabe3.ui.add

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * Add screen
 *
 * @param navController
 * @param sharedViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // Function to open DatePickerDialog


    // Define color range
    val startColor = Color(0xFF008577) // Teal
    val endColor = Color(0xFFD81B60) // Pink

    // Function to calculate intermediate colors
    /**
     * Calculate color
     *
     * @param id
     * @param totalEntries
     * @return
     */
    fun calculateColor(id: Int, totalEntries: Int): Color {
        val fraction = id.toFloat() / totalEntries.toFloat()
        val red = (startColor.red + fraction * (endColor.red - startColor.red)).coerceIn(0f, 1f)
        val green = (startColor.green + fraction * (endColor.green - startColor.green)).coerceIn(0f, 1f)
        val blue = (startColor.blue + fraction * (endColor.blue - startColor.blue)).coerceIn(0f, 1f)
        return Color(red, green, blue)
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Range Field
            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate?.format(dateFormatter)} - ${departureDate?.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = { },
                label = { Text("Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateRangePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isEmpty() || arrivalDate == null || departureDate == null) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    } else if (arrivalDate!!.isAfter(departureDate)) {
                        Toast.makeText(context, "Arrival date cannot be after departure date", Toast.LENGTH_SHORT).show()
                    } else {
                        val totalEntries = sharedViewModel.bookingsEntries.value.size
                        val color = calculateColor(totalEntries, totalEntries + 1)
                        sharedViewModel.addBookingEntry(
                            BookingEntry(id = 0, name = name, arrivalDate!!, departureDate!!, color)
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }

    if (showDateRangePicker) {
        DateRangePickerDialog(
            onDateSelected = { startDate, endDate ->
                arrivalDate = startDate
                departureDate = endDate
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
    }
}


/**
 * Date range picker dialog
 *
 * @param onDateSelected
 * @param onDismiss
 * @receiver
 * @receiver
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDateSelected: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateTime = LocalDateTime.now()

    val dateRangePickerState = remember {
        DateRangePickerState(
            initialSelectedStartDateMillis = dateTime.toMillis(),
            initialDisplayedMonthMillis = null,
            initialSelectedEndDateMillis = dateTime.plusDays(3).toMillis(),
            initialDisplayMode = DisplayMode.Picker,
            yearRange = (2023..2024)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date Range") },
        confirmButton = {
            Button(onClick = {
                val startDateMillis = dateRangePickerState.selectedStartDateMillis
                val endDateMillis = dateRangePickerState.selectedEndDateMillis

                if (startDateMillis != null && endDateMillis != null) {
                    val startDate = LocalDate.ofInstant(
                        Instant.ofEpochMilli(startDateMillis),
                        ZoneId.systemDefault()
                    )
                    val endDate = LocalDate.ofInstant(
                        Instant.ofEpochMilli(endDateMillis),
                        ZoneId.systemDefault()
                    )
                    onDateSelected(startDate, endDate)
                } else {
                    Toast.makeText(context, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
                }

                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            DateRangePicker(state = dateRangePickerState)
        },
        modifier = Modifier.fillMaxWidth()
        ,

    )
}

/**
 * To millis
 *
 */
fun LocalDateTime.toMillis() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()



