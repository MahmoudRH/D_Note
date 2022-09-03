package com.mahmoudrh.roomxml.domain.usecases

import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.repository.NotesRepository
import com.mahmoudrh.roomxml.domain.utils.OrderBy
import com.mahmoudrh.roomxml.domain.utils.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNotes(private val repository: NotesRepository) {
    operator fun invoke(orderBy: OrderBy = OrderBy.Date(OrderType.Descending)): Flow<List<Note>> {
        return repository.getAllNotes().map { notesList ->
            when (orderBy.orderType) {
                is OrderType.Descending -> {
                    when (orderBy) {
                        is OrderBy.Title -> notesList.sortedByDescending { it.title.lowercase() }
                        is OrderBy.Date -> notesList.sortedByDescending { it.date.toLong() }
                    }
                }
                is OrderType.Ascending -> {
                    when (orderBy) {
                        is OrderBy.Title -> notesList.sortedBy { it.title.lowercase() }
                        is OrderBy.Date -> notesList.sortedBy { it.date.toLong() }
                    }
                }
            }
        }
    }
}