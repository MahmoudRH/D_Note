package com.mahmoudrh.roomxml.presentation.ui_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    searchWord: MutableState<String>,
    hint: String,
    focusRequester: FocusRequester,
    onSearch: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused)
                    keyboardController?.show()
            },
        value = searchWord.value,
        onValueChange = { searchWord.value = it },
        singleLine = true,
        decorationBox = { innerTextField ->
            AnimatedVisibility(
                searchWord.value.isEmpty(),
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Text(text = hint, color = Color.Gray, fontSize = 18.sp)
            }
            if (searchWord.value.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            searchWord.value = " "
                            // I Had to put the space and trim it in the view model to avoid crash caused by
                            // BasicTextField (https://issuetracker.google.com/issues/229378536)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Clear"
                        )
                    }
                    innerTextField()
                }
            } else
                innerTextField()
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            onSearch()
        }),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
    )
}