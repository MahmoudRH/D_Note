package com.mahmoudrh.roomxml.presentation.screens.all_notes

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahmoudrh.roomxml.presentation.screens.destinations.NoteScreenDestination
import com.mahmoudrh.roomxml.presentation.screens.destinations.SearchScreenDestination
import com.mahmoudrh.roomxml.presentation.ui_components.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllNotesScreen(
    viewModel: AllNotesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val notesState = viewModel.notesState.value
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            AppTopBars.DefaultTopBar(title = "D Note", actionIcon = Icons.Default.Search) {
                navigator.navigate(SearchScreenDestination)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.navigate(NoteScreenDestination.route) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Note")
            }
        }, snackbarHost = { SnackbarHost(snackBarHostState) }
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
                        text = "Sort ",
                    )
                    IconButton(onClick = { viewModel.onEvent(AllNotesEvent.ToggleOrderSection) }) {
                        Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort")
                    }
                }
                AnimatedVisibility(
                    visible = notesState.isOrderSectionVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    OrderSection(
                        order = notesState.order,
                        onOrderChange = {
                            viewModel.onEvent(AllNotesEvent.Order(it))
                        }
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = viewModel.isSelectionModeEnabled.value,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = { viewModel.onEvent(AllNotesEvent.DeleteSelectedNotes) }) {
                            Text(text = "Delete Selected")
                        }
                        Button(onClick = {
                            viewModel.onEvent(AllNotesEvent.DeleteAllNotes)
                        }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                            Text(text = "Delete All")
                        }
                    }
                }
            }
            items(notesState.notes, key = { it.id }) {

                NoteItem(
                    modifier = Modifier.padding(vertical = 8.dp),
                    note = it,
                    isSelectionModeEnabled = viewModel.isSelectionModeEnabled,
                    onClick = { navigator.navigate(NoteScreenDestination(it)) },
                    onLongClick = {
                        viewModel.onEvent(AllNotesEvent.SelectNote(it))
                    },
                    onSwipeOut = {
                        viewModel.onEvent(AllNotesEvent.DeleteNote(it))
                        scope.launch {
                            val result = snackBarHostState.showSnackbar(
                                message = "Note deleted",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.onEvent(AllNotesEvent.RestoreNote)
                            }
                        }
                    }
                )
            }
        }
        EmptyListScreen(visibility = notesState.isListEmpty, text = "Add Some Notes!")
        LoadingScreen(visibility = notesState.isListLoading)
    }
}