package com.mahmoudrh.roomxml.presentation.screens.search


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahmoudrh.roomxml.presentation.screens.destinations.NoteScreenDestination
import com.mahmoudrh.roomxml.presentation.ui_components.AppTopBars
import com.mahmoudrh.roomxml.presentation.ui_components.EmptyListScreen
import com.mahmoudrh.roomxml.presentation.ui_components.LoadingScreen
import com.mahmoudrh.roomxml.presentation.ui_components.NoteItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val focusRequester = FocusRequester()

    Column(Modifier.fillMaxSize()) {
        AppTopBars.SearchTopBar(
            onNavigateBack = { navigator.popBackStack() },
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
                        onClick = { navigator.navigate(NoteScreenDestination(it)) },
                    )
                }
            }
            EmptyListScreen(
                visibility = viewModel.isResultsListEmpty.value,
                text = "Sorry, We Couldn't Find Any Results.. ",
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