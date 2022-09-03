package com.mahmoudrh.roomxml.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    fun formatDate(noteDate: String): String {
        val timeStamp = noteDate.toLong()
        val date = Date(timeStamp)
        return SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}