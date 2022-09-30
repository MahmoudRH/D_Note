package com.mahmoudrh.roomxml.presentation.ui_components

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.presentation.utils.DateFormatter
import kotlinx.coroutines.launch
import java.lang.ArithmeticException
import kotlin.math.absoluteValue


@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    isSelectionModeEnabled: MutableState<Boolean>,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSwipeOut: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var offsetX by mutableStateOf(0f)
    var alpha by mutableStateOf(0f)
    val vanishOffsetRight = remember { Animatable(250f) }
    val vanishOffsetLeft = remember { Animatable(-250f) }

    var isSelected by remember { mutableStateOf(note.isSelected) }
    fun resetOffset() {
        scope.launch {
            val resetOffset = Animatable(offsetX)
            resetOffset.animateTo(0f, tween(180)) { offsetX = value }
        }
    }

    fun swipeOut() {
        scope.launch {
            launch {
                val vanishAlpha = Animatable(0.3f)
                vanishAlpha.animateTo(1f, tween(80)) { alpha = value }
            }
            launch {
                if (offsetX > 0)
                    vanishOffsetRight.animateTo(1030f, tween(80)) { offsetX = value }
                else
                    vanishOffsetLeft.animateTo(-1050f, tween(80)) { offsetX = value }
            }
        }.invokeOnCompletion {
            onSwipeOut()
        }
    }
    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = try {
                    (offsetX) / 1000
                } catch (e: ArithmeticException) {
                    1f
                }
                this.alpha = (1f - alpha.absoluteValue).coerceIn(0f, 1f)
                this.translationX = offsetX
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (isSelectionModeEnabled.value) {
                            isSelected = !isSelected
                            onLongClick()
                        } else
                            onClick()
                    },
                    onLongPress = {
                        isSelected = !isSelected
                        onLongClick()
                    })
            }
            .draggable(
                rememberDraggableState(onDelta = { delta ->
                    offsetX += delta
                }),
                onDragStopped = { velocity ->
                    if (velocity.absoluteValue < 2000) {
                        resetOffset()
                    } else {
                        swipeOut()
                    }
                },
                orientation = Orientation.Horizontal
            )
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
            Divider(
                color = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
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
            Divider(
                color = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
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
