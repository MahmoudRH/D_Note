package com.mahmoudrh.roomxml.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withStyle

val TAG = "Mah "

fun buildAnnotatedStringFrom(text: String): AnnotatedString {
    var text_ = text
    val allWordsMap = mutableMapOf<String, ContentTransformationHelper>()
    availableStylesList.forEach { style ->
        val matches = style.regex.findAll(text_).map { it.value to style }
        allWordsMap.putAll(matches)
    }
    val builder = AnnotatedString.Builder()
    if (allWordsMap.isEmpty()) {
        builder.append(text_)
        return builder.toAnnotatedString()
    }
    /***Index based sort*/
    val sortedWordsMap =
        allWordsMap.toList().sortedBy { (value, _) -> text_.indexOf(value) }.toMap()

    var pointer = 0
    sortedWordsMap.forEach { (word, style) ->

        val wordIndex = text_.indexOf(word)
        if (wordIndex < 0) { return@forEach }
        val part = text_.substring(pointer, wordIndex)
        builder.append(part)

        val styledWord = word.removeSurrounding(style.prefix, style.suffix)
        builder.withStyle(style.spanStyle) { append(styledWord) }
        pointer = wordIndex + styledWord.length

        // if the style is applied to the whole text
        if (wordIndex + word.length == text_.length) pointer = 0
        text_ = text_.replaceRange(
            startIndex = wordIndex,
            endIndex = wordIndex + word.length,
            replacement = styledWord
        )
    }
    if (pointer != 0)
        builder.append(text_.substring(pointer))

    return builder.toAnnotatedString()
}

/*class ContentTransformation() : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedString(text.toString()),
            OffsetMapping.Identity
        )
    }

    private fun buildAnnotatedString(text: String): AnnotatedString {
        var text_ = text
        val allWordsMap = mutableMapOf<String, ContentTransformationHelper>()
        availableStylesList.forEach { style ->
            val matches = style.regex.findAll(text_).map { it.value to style }
            allWordsMap.putAll(matches)
        }
        val builder = AnnotatedString.Builder()
        if (allWordsMap.isEmpty()) {
            builder.append(text_)
            return builder.toAnnotatedString()
        }

        val sortedWordsMap =
            allWordsMap.toList().sortedBy { (value, _) -> text_.indexOf(value) }.toMap()

        var pointer = 0
        sortedWordsMap.forEach { (word, style) ->
            Log.e(
                TAG,
                "allWordsMap:[$word], style[${
                    style.javaClass.name.let {
                        it.substring(it.lastIndexOf('$'))
                    }
                }] "
            )
            val wordIndex = text_.indexOf(word)
            Log.e(TAG, "text_[$text_] ")
            Log.e(TAG, "wordIndex[$wordIndex]")
            Log.e(TAG, "pointer[$pointer]")
            if (wordIndex < 0) {
                Log.e(TAG, "word not found!!!:"); return@forEach
            }
            val part = text_.substring(pointer, wordIndex)
            builder.append(part)

            val styledWord = word.removeSurrounding(style.prefix, style.suffix)
            builder.withStyle(style.spanStyle) {
                Log.e(TAG, "styledWord[$styledWord] ")
                append(styledWord)
            }
            pointer = wordIndex + styledWord.length

            //if the style is applied to the whole text
            if (wordIndex + word.length == text_.length) pointer = 0
            Log.e(TAG, "pointer[$pointer]")
            text_ = text_.replaceRange(
                startIndex = wordIndex,
                endIndex = wordIndex + word.length,
                replacement = styledWord
            )
            Log.e(TAG, "text_[$text_], text_.length[${text_.length}] ")
        }
        if (pointer != 0)
            builder.append(text_.substring(pointer))
//        if (text_.isNotEmpty())
//            builder.append(text_)
        return builder.toAnnotatedString()
    }
}*/
