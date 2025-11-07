package com.mahmoudrh.roomxml.presentation.screens.all_notes

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.utils.OrderBy
import com.mahmoudrh.roomxml.presentation.ui_components.*
import kotlinx.coroutines.launch

@Composable
fun AllNotesScreen(
    viewModel: AllNotesViewModel = hiltViewModel(),
    onNoteClicked: (Int?) -> Unit,
    onClickSearch: () -> Unit,
) {
    val notesState = viewModel.notesState.value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    AllNotesUI(
        notesState = notesState,
        onClickSearch = { onClickSearch() },
        onClickAdd = { onNoteClicked(null) },
        onClickOrder = { viewModel.onEvent(AllNotesEvent.ToggleOrderSection) },
        onOrderChanged = { viewModel.onEvent(AllNotesEvent.Order(it)) },
        onClickDeleteSelectedNote = { viewModel.onEvent(AllNotesEvent.DeleteSelectedNotes) },
        onClickDeleteAll = { viewModel.onEvent(AllNotesEvent.DeleteAllNotes) },
        onClickNoteItem = { onNoteClicked(it.id) },
        onLongClickNote = { viewModel.onEvent(AllNotesEvent.SelectNote(it)) }
    ) {
        viewModel.onEvent(AllNotesEvent.DeleteNote(it))
        scope.launch {
            val result = notesState.snackbarHostState.showSnackbar(
                message = context.getString(R.string.note_deleted),
                actionLabel = context.getString(R.string.undo)
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onEvent(AllNotesEvent.RestoreNote)
            }
        }
    }
}

@Composable
private fun AllNotesUI(
    notesState: NotesState,
    onClickSearch: () -> Unit,
    onClickAdd: () -> Unit,
    onClickOrder: () -> Unit,
    onOrderChanged: (OrderBy) -> Unit,
    onClickDeleteSelectedNote: () -> Unit,
    onClickDeleteAll: () -> Unit,
    onClickNoteItem: (Note) -> Unit,
    onLongClickNote: (Note) -> Unit,
    onSwipeNoteOut: (Note) -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBars.DefaultTopBar(
                title = stringResource(R.string.d_note),
                actionIcon = Icons.Default.Search,
                onActionClick = onClickSearch
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = onClickAdd) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.new_note)
                )
            }
        }, snackbarHost = { SnackbarHost(notesState.snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sort),
                    )
                    IconButton(onClick = onClickOrder) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = stringResource(R.string.sort)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = notesState.isOrderSectionVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    OrderSection(
                        order = notesState.order,
                        onOrderChange = onOrderChanged
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = notesState.isSelectionModeEnabled,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = onClickDeleteSelectedNote) {
                            Text(text = stringResource(R.string.delete_selected))
                        }
                        Button(
                            onClick = onClickDeleteAll,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text(text = stringResource(R.string.delete_all))
                        }
                    }
                }
            }
            items(notesState.notes, key = { it.id }) { note ->

                NoteItem(
                    modifier = Modifier.padding(vertical = 8.dp),
                    note = note,
                    isSelectionModeEnabled = notesState.isSelectionModeEnabled,
                    onClick = { onClickNoteItem(note) },
                    onLongClick = { onLongClickNote(note) },
                    onSwipeOut = { onSwipeNoteOut(note) }
                )
            }
        }
        EmptyListScreen(
            visibility = notesState.isListEmpty,
            text = stringResource(R.string.add_some_notes)
        )
        LoadingScreen(visibility = notesState.isListLoading)
    }
}

@Preview
@Composable
private fun NotesScreenPreview() {
    AllNotesUI(
        notesState = NotesState(
            isListLoading = false,
            notes = listOf(
                Note(
                    id = 1,
                    title = "Title",
                    content = "Content",
                    date = System.currentTimeMillis().toString()
                )
            )
        ),
        onClickSearch = {},
        onClickAdd = {},
        onClickOrder = {},
        onOrderChanged = {},
        onClickDeleteSelectedNote = {},
        onClickDeleteAll = {},
        onClickNoteItem = {},
        onLongClickNote = {},
        onSwipeNoteOut = {}
    )
}
