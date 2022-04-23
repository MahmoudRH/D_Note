package com.mahmoudrh.roomxml.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {
    companion object{
        fun formatDate(noteDate: String): String {
            val timeStamp = noteDate.toLong()
            val date = Date(timeStamp)
            return SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
        }
    }
}