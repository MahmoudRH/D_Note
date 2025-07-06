package com.mahmoudrh.roomxml.presentation.screens.all_notes

import androidx.compose.material3.SnackbarHostState
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.utils.OrderBy
import com.mahmoudrh.roomxml.domain.utils.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val order: OrderBy = OrderBy.Date(OrderType.Descending),
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val isSelectionModeEnabled: Boolean = false,
    val isOrderSectionVisible: Boolean = false,
    val isListLoading: Boolean = true,
    val isListEmpty: Boolean = false,
)