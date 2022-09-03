package com.mahmoudrh.roomxml.presentation.ui_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyListScreen(visibility: Boolean, text: String, fontSize: TextUnit = 20.sp) {
    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                text = text,
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Thin,
                textAlign = TextAlign.Center
            )
        }
    }
}