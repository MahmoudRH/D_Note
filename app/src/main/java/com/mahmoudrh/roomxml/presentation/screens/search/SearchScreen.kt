package com.mahmoudrh.roomxml.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.presentation.ui_components.AppTopBars
import com.mahmoudrh.roomxml.presentation.ui_components.EmptyListScreen
import com.mahmoudrh.roomxml.presentation.ui_components.LoadingScreen
import com.mahmoudrh.roomxml.presentation.ui_components.NoteItem

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onClickNote: (Int) -> Unit,
) {
    val focusRequester = FocusRequester()

    Column(Modifier.fillMaxSize()) {
        AppTopBars.SearchTopBar(
            onNavigateBack = onNavigateBack,
            hint = "Search..",
            focusRequester = focusRequester,
            searchWord = viewModel.searchWord,
            onSearch = { viewModel.search() }
        )
        Box(Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
            ) {
                items(viewModel.resultsList, key = { it.id }) {
                    NoteItem(
                        modifier = Modifier.padding(vertical = 8.dp),
                        note = it,
                        onClick = { onClickNote(it.id) },
                    )
                }
            }
            EmptyListScreen(
                visibility = viewModel.isResultsListEmpty.value,
                text = stringResource(R.string.sorry_we_couldn_t_find_any_results),
                fontSize = 20.sp
            )
            LoadingScreen(visibility = viewModel.isLoading.value)
        }
    }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}