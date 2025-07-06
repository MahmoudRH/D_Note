package com.mahmoudrh.roomxml.presentation.screens.note

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.presentation.ui_components.AppTopBars
import com.mahmoudrh.roomxml.presentation.utils.buildAnnotatedStringFrom
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

private enum class ViewType(val value: Int) {
    ViewOnly(0),
    EditMode(1)
}

@Destination<RootGraph>(navArgs = NoteNavArgs::class)
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun NoteScreen(viewModel: NoteViewModel = hiltViewModel(), navigator: DestinationsNavigator) {

    val eventName = viewModel.eventName
    val icon = if (viewModel.isEditModeEnabled.value) Icons.Default.Check else Icons.Default.Edit
    val text =
        if (viewModel.isEditModeEnabled.value) stringResource(R.string.save) else stringResource(R.string.edit)
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        pagerState.animateScrollToPage(
            if (viewModel.isEditModeEnabled.value) ViewType.EditMode.value else ViewType.ViewOnly.value
        )
    }

    NoteUI(
        eventName = stringResource(eventName.intValue),
        isActionIconVisible = !viewModel.isEditModeEnabled.value || viewModel.canDoAction.value,
        pagerState = pagerState,
        text = text,
        icon = icon,
        onClickNavigateBack = {
            if (viewModel.isEditModeEnabled.value) {
                viewModel.onEvent(NoteEvent.ToggleEditMode)
                if (viewModel.note != null) {
                    scope.launch {
                        //add check for null, else popBackStack
                        pagerState.animateScrollToPage(ViewType.ViewOnly.value)
                    }
                    keyboardController?.hide()
                } else {
                    navigator.popBackStack()
                }
            } else {
                navigator.popBackStack()
            }
        },
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
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        noteTitle = viewModel.noteTitle.value,
        noteContent = viewModel.noteContent.value,
        isTitleError = viewModel.isTitleError.value,
        isContentError = viewModel.isContentError.value,


        )

    if (viewModel.isEventSuccess.value) {
        Toast.makeText(
            LocalContext.current,
            stringResource(R.string.success, stringResource(eventName.intValue)), Toast.LENGTH_SHORT
        ).show()
        navigator.popBackStack()
    }
}

@Composable
private fun NoteUI(
    eventName: String,
    noteTitle: String,
    noteContent: String,
    isTitleError: Boolean,
    isContentError: Boolean,
    isActionIconVisible: Boolean,
    pagerState: PagerState,
    text: String,
    icon: ImageVector,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onClickNavigateBack: () -> Unit,
    onActionClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBars.DefaultTopBar(
                title = eventName,
                onNavigateBack = onClickNavigateBack,
                actionText = text,
                actionIcon = icon,
                onActionClick = onActionClick,
                actionIconVisibility = isActionIconVisible
            )
        },
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            state = pagerState,
            userScrollEnabled = false,
        ) { page ->
            when (page) {
                ViewType.ViewOnly.value -> {
                    ViewingNote(
                        title = noteTitle,
                        content = noteContent
                    )
                }

                ViewType.EditMode.value -> {
                    EditingNote(
                        noteTitle = noteTitle,
                        onTitleChange = { onTitleChange(it) },
                        isTitleError = isTitleError,
                        noteContent = noteContent,
                        isContentError = isContentError,
                        onContentChange = { onContentChange(it) }
                    )
                }
            }
        }
    }
}

data class NoteNavArgs(val note: Note?)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditingNote(
    noteTitle: String,
    onTitleChange: (String) -> Unit,
    isTitleError: Boolean,
    noteContent: String,
    isContentError: Boolean,
    onContentChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = noteTitle,
            onValueChange = onTitleChange,
            label = { Text(text = stringResource(R.string.title)) },
            singleLine = true,
            placeholder = { Text(text = stringResource(R.string.add_a_title)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            isError = isTitleError
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxSize().imePadding(),
            value = noteContent,
            onValueChange = onContentChange,
            label = { Text(text = stringResource(R.string.content)) },
            placeholder = { Text(text = stringResource(R.string.add_some_content)) },
            isError = isContentError,
        )
    }
}

@Composable
private fun ViewingNote(title: String, content: String) {
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
        HorizontalDivider()
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

@Preview
@Composable
private fun NoteUIPrev() {
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    val pagerState = rememberPagerState(1) { 2 }

    NoteUI(
        eventName = stringResource(R.string.adding_new_note),
        noteTitle = noteTitle,
        onTitleChange = { noteTitle = it },
        isTitleError = false,
        noteContent = noteContent,
        isContentError = false,
        onContentChange = { noteContent = it },
        isActionIconVisible = false,
        pagerState = pagerState,
        text = "",
        icon = Icons.Default.Check,
        onClickNavigateBack = {},
        onActionClick = {},
    )
}


@Preview(showBackground = true)
@Composable
fun ViewingNotePreview() {
    ViewingNote(
        title = "Sample Note Title",
        content = "This is the content of a note.\nYou can scroll through this text to simulate a long note."
    )
}

@Preview(showBackground = true)
@Composable
fun EditingNotePreview() {
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    EditingNote(
        noteTitle = noteTitle,
        onTitleChange = { noteTitle = it },
        isTitleError = false,
        noteContent = noteContent,
        isContentError = false,
        onContentChange = { noteContent = it }
    )
}