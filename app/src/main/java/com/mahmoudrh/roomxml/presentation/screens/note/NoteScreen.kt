package com.mahmoudrh.roomxml.presentation.screens.note

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.presentation.ui_components.AppTopBars
import com.mahmoudrh.roomxml.presentation.utils.buildAnnotatedStringFrom
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

private enum class ViewType(val value: Int) {
    ViewOnly(0),
    EditMode(1)
}

@Destination(navArgsDelegate = NoteNavArgs::class)
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun NoteScreen(viewModel: NoteViewModel = hiltViewModel(), navigator: DestinationsNavigator) {

    val eventName = viewModel.eventName
    val icon = if (viewModel.isEditModeEnabled.value) Icons.Default.Check else Icons.Default.Edit
    val text = if (viewModel.isEditModeEnabled.value) "Save" else "Edit"
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        pagerState.animateScrollToPage(
            if (viewModel.isEditModeEnabled.value) ViewType.EditMode.value else ViewType.ViewOnly.value
        )
    }

    Scaffold(
        topBar = {
            AppTopBars.DefaultTopBar(
                title = eventName.value,
                onNavigateBack = {
                    if (viewModel.isEditModeEnabled.value) {
                        viewModel.onEvent(NoteEvent.ToggleEditMode)
                        scope.launch {
                            pagerState.animateScrollToPage(ViewType.ViewOnly.value)
                        }
                        keyboardController?.hide()
                    } else {
                        navigator.popBackStack()
                    }
                },
                actionText = text,
                actionIcon = icon,
                onActionClick = {
                    if (viewModel.isEditModeEnabled.value) {
                        if (viewModel.isUpdatingNote()) {
                            viewModel.onEvent(NoteEvent.UpdateNote)
                        } else {
                            viewModel.onEvent(NoteEvent.InsertNote)
                        }
                    } else {
                        viewModel.onEvent(NoteEvent.ToggleEditMode)
                        scope.launch {
                            pagerState.animateScrollToPage(
                                if (viewModel.isEditModeEnabled.value) ViewType.EditMode.value else ViewType.ViewOnly.value
                            )
                        }
                    }
                },
                actionIconVisibility = !viewModel.isEditModeEnabled.value || viewModel.canDoAction.value
            )
        },
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            state = pagerState,
            count = 2,
            userScrollEnabled = false,
        ) { page ->
            when (page) {
                ViewType.ViewOnly.value -> {
                    ViewingNote(title = viewModel.noteTitle.value, content = viewModel.noteContent.value)
                }
                ViewType.EditMode.value -> {
                    EditingNote(viewModel = viewModel)
                }
            }
        }
    }

    if (viewModel.isEventSuccess.value) {
        Toast.makeText(LocalContext.current, "${eventName.value} Success", Toast.LENGTH_SHORT)
            .show()
        navigator.popBackStack()
    }
}

data class NoteNavArgs(val note: Note?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditingNote(viewModel: NoteViewModel) {
    val focusManager = LocalFocusManager.current
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.noteTitle.value,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text(text = "Title") },
            singleLine = true,
            placeholder = { Text(text = "Add a title...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            isError = viewModel.isTitleError.value
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxSize(),
            value = viewModel.noteContent.value,
            onValueChange = { viewModel.onContentChange(it) },
            label = { Text(text = "Content") },
            placeholder = { Text(text = "Add some content...") },
            isError = viewModel.isContentError.value,
        )
    }
}

@Composable
fun ViewingNote(title: String, content: String) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp),
            text = buildAnnotatedStringFrom(title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.size(8.dp))
        Divider()
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            text = buildAnnotatedStringFrom(content),
        )
    }
}