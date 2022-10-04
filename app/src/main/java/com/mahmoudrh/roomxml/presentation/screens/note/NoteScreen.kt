package com.mahmoudrh.roomxml.presentation.screens.note

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.presentation.ui_components.AppTopBars
import com.mahmoudrh.roomxml.presentation.utils.ContentTransformation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(navArgsDelegate = NoteNavArgs::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(viewModel: NoteViewModel = hiltViewModel(), navigator: DestinationsNavigator) {

    val focusManager = LocalFocusManager.current
    val eventName = viewModel.eventName
    val icon = if (viewModel.isEditModeEnabled.value) Icons.Default.Check else Icons.Default.Edit
    val text = if (viewModel.isEditModeEnabled.value) "Save" else "Edit"
    Scaffold(
        topBar = {
            AppTopBars.DefaultTopBar(
                title = eventName.value,
                onNavigateBack = { navigator.popBackStack() },
                actionText = text,
                actionIcon = icon,
                onActionClick = {
                    if(viewModel.isEditModeEnabled.value) {
                        if (viewModel.isUpdatingNote()) {
                            viewModel.onEvent(NoteEvent.UpdateNote)
                        } else {
                            viewModel.onEvent(NoteEvent.InsertNote)
                        }
                    }else{
                        viewModel.onEvent(NoteEvent.ToggleEditMode)
                    }
                },
                actionIconVisibility = viewModel.canDoAction.value
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.noteTitle.value,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text(text = "Title") },
                singleLine = true,
                enabled = viewModel.isEditModeEnabled.value,
                placeholder = { Text(text = "Add some title...") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                isError = viewModel.isTitleError.value
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxSize(),
                value = viewModel.noteContent.value,
                onValueChange = { viewModel.onContentChange(it) },
                label = { Text(text = "Content") },
                enabled = viewModel.isEditModeEnabled.value,
                placeholder = { Text(text = "Add some content...") },
                isError = viewModel.isContentError.value,
                visualTransformation = ContentTransformation(viewModel.isEditModeEnabled.value)
            )
        }
    }

    if (viewModel.isEventSuccess.value) {
        Toast.makeText(LocalContext.current, "${eventName.value} Success", Toast.LENGTH_SHORT)
            .show()
        navigator.popBackStack()
    }

}


data class NoteNavArgs(val note: Note?)