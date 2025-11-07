package com.mahmoudrh.roomxml.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mahmoudrh.roomxml.presentation.screens.all_notes.AllNotesScreen
import com.mahmoudrh.roomxml.presentation.screens.note.NoteScreen
import com.mahmoudrh.roomxml.presentation.screens.search.SearchScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "all_notes") {
        composable("all_notes") {
            AllNotesScreen(
                onNoteClicked = { noteId -> navController.navigate("note/$noteId") },
                onClickSearch = { navController.navigate("search") }
            )
        }
        composable(
            route = "note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType; nullable = true })
        ) {
            NoteScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("search") {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onClickNote = { noteId -> navController.navigate("note/$noteId") }
            )
        }
    }
}