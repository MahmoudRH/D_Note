package com.mahmoudrh.roomxml.presentation.ui_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.presentation.utils.DateFormatter
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    isSelectionModeEnabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSwipeOut: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var offsetX by remember { mutableFloatStateOf(0f) }
    var alpha by remember { mutableFloatStateOf(0f) }
    var isSelected by remember { mutableStateOf(note.isSelected) }

    fun resetOffset() {
        scope.launch {
            Animatable(offsetX).animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 180)
            ) { offsetX = value }
        }
    }

    fun swipeOut() {
        scope.launch {
            // Fade out while swiping
            val fadeOut = launch {
                Animatable(0.3f).animateTo(
                    targetValue = 1f,
                    animationSpec = tween(80)
                ) { alpha = value }
            }

            // Slide off-screen
            val vanishAnim = Animatable(offsetX)
            val targetOffset = if (offsetX > 0) 1030f else -1050f

            val slideOut = launch {
                vanishAnim.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(80)
                ) { offsetX = value }
            }

            fadeOut.join()
            slideOut.join()

            onSwipeOut()
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                this.alpha = (1f - alpha.absoluteValue).coerceIn(0f, 1f)
                translationX = offsetX
            }
            .combinedClickable(
                onClick = {
                    if (isSelectionModeEnabled) {
                        isSelected = !isSelected
                        onLongClick()
                    } else {
                        onClick()
                    }
                },
                onLongClick = {
                    isSelected = !isSelected
                    onLongClick()
                }
            )
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                },
                onDragStopped = { velocity ->
                    if (velocity.absoluteValue < 2000) {
                        resetOffset()
                    } else {
                        swipeOut()
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
                    .background(MaterialTheme.colorScheme.primary)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(8f)
            ) {
                Text(
                    text = note.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.Bottom)
                    .padding(bottom = 8.dp, end = 8.dp),
                text = DateFormatter.formatDate(note.date),
                textAlign = TextAlign.End,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(8f)
            ) {
                Text(
                    text = note.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.Bottom)
                    .padding(bottom = 8.dp, end = 8.dp),
                text = DateFormatter.formatDate(note.date),
                textAlign = TextAlign.End,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteItemFullPreview() {
    NoteItem(
        note = Note(
            title = "Title",
            content = "Content",
            date = System.currentTimeMillis().toString()
        ),
        isSelectionModeEnabled = true,
        onClick = {},
        onLongClick = {},
        onSwipeOut = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoteItemSimplePreview() {
    NoteItem(
        note = Note(
            title = "Compose FTW",
            content = "Previewing the simple note item without swipe or selection.",
            date = System.currentTimeMillis().toString()
        ),
        onClick = {}
    )
}
