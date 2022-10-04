package com.mahmoudrh.roomxml.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class ContentTransformation(private val editMode: Boolean = true) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedString(text.toString(), editMode = editMode),
            OffsetMapping.Identity
        )
    }

    private fun buildAnnotatedString(text: String, editMode: Boolean): AnnotatedString {
        var text_ = text
        val boldRegex = Regex("\\*[^*]+\\*")
        val matches = boldRegex.findAll(text_)
        val boldWordsList = matches.map { it.value }.toList()
        val builder = AnnotatedString.Builder()
        boldWordsList.forEach {
            val boldWordIndex = text_.indexOf(it)
            val part = text_.substring(0, boldWordIndex)
            builder.append(part)
            builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                if (editMode) {
                    append(it)
                } else {
                    val boldWord = it.removeSurrounding("*", "*")
                    append(boldWord)
                }
            }
            text_ = text_.drop(boldWordIndex).drop(it.length)
        }
        if (text_.isNotEmpty())
            builder.append(text_)
        return builder.toAnnotatedString()
    }
}
