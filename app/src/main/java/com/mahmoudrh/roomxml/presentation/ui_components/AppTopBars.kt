package com.mahmoudrh.roomxml.presentation.ui_components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


object AppTopBars {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DefaultTopBar(
        title: String,
        onNavigateBack: () -> Unit,
        actionIcon: ImageVector,
        onActionClick: () -> Unit
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier.shadow(elevation = 8.dp),
            title = { Text(text = title) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp),
            ),
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = onActionClick) {
                    Icon(actionIcon, contentDescription = null)
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DefaultTopBar(
        title: String,
        actionIcon: ImageVector,
        onActionClick: () -> Unit
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier.shadow(elevation = 8.dp),
            title = { Text(text = title) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp),
            ),
            actions = {
                IconButton(onClick = onActionClick) {
                    Icon(actionIcon, contentDescription = null)
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchTopBar(
        onNavigateBack: () -> Unit = {},
        searchWord: MutableState<String>,
        onSearch: () -> Unit = {},
        hint: String,
        focusRequester: FocusRequester
    ) {
        SmallTopAppBar(
            modifier = Modifier.shadow(elevation = 8.dp),
            title = {
                SearchTextField(
                    searchWord = searchWord,
                    hint = hint,
                    focusRequester = focusRequester
                ) {
                    if (searchWord.value.isNotEmpty())
                        onSearch()
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp),
            ),
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            })
    }

}
