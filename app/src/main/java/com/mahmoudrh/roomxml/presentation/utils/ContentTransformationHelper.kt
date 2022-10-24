package com.mahmoudrh.roomxml.presentation.utils

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

sealed class ContentTransformationHelper(
    val regex: Regex,
    val spanStyle: SpanStyle,
    val prefix: String,
    val suffix: String
) {

    object UnderLined : ContentTransformationHelper(
        UNDER_LINED_REGEX, UNDER_LINED_SPAN_STYLE, UNDER_LINED_PREFIX, UNDER_LINED_SUFFIX
    )

    object Bold : ContentTransformationHelper(BOLD_REGEX, BOLD_SPAN_STYLE, BOLD_PREFIX, BOLD_SUFFIX)
    object Italic :
        ContentTransformationHelper(ITALIC_REGEX, ITALIC_SPAN_STYLE, ITALIC_PREFIX, ITALIC_SUFFIX)

    private companion object {
        val BOLD_REGEX = Regex(pattern = "[*]{2,}([^*]|[^*]\\*|\\*[^*])+[*]{2,}")
        val BOLD_SPAN_STYLE = SpanStyle(fontWeight = FontWeight.Bold)
        const val BOLD_PREFIX = "**"
        const val BOLD_SUFFIX = "**"

        val UNDER_LINED_REGEX = Regex(pattern = "_[^_]+_")
        val UNDER_LINED_SPAN_STYLE = SpanStyle(textDecoration = TextDecoration.Underline)
        const val UNDER_LINED_PREFIX = "_"
        const val UNDER_LINED_SUFFIX = "_"

        val ITALIC_REGEX = Regex(pattern = "@[^@]+@")
        val ITALIC_SPAN_STYLE = SpanStyle(fontStyle = FontStyle.Italic)
        const val ITALIC_PREFIX = "@"
        const val ITALIC_SUFFIX = "@"
    }
}

// val RESERVED_SPECIAL_CHARACTERS = Regex("[@_]|[*]{2}")
val availableStylesList: List<ContentTransformationHelper> by lazy {
    val itemsList = mutableListOf<ContentTransformationHelper>()
    ContentTransformationHelper::class.sealedSubclasses.forEach { kClass ->
        val styleInstance = kClass.objectInstance
        styleInstance?.let {
            itemsList.add(styleInstance)
        }
    }
    itemsList
}
