package com.mahmoudrh.roomxml.presentation.ui_components


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        actionText: String,
        actionIconVisibility: Boolean = false,
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
                AnimatedVisibility(
                    visible = actionIconVisibility,
                    enter = expandIn(expandFrom = Alignment.Center),
                    exit =  fadeOut(tween(200))
                ) {
                    Row(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onActionClick() }
                            .padding(vertical = 5.dp, horizontal = 5.dp)

                    ) {
                        Icon(imageVector = actionIcon, contentDescription = "$actionText Note")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = actionText)
                    }
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
