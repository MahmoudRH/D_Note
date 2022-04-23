package com.mahmoudrh.roomxml

import androidx.lifecycle.*
import com.mahmoudrh.roomxml.data.Note
import com.mahmoudrh.roomxml.data.NotesRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {

    var isSelectionModeEnabled = MutableLiveData(false)
    val selectedNotes = mutableListOf<Note>()
    val allNotes: LiveData<List<Note>> = repository.allNotes.asLiveData()

    fun searchNotes(searchWord: String): LiveData<List<Note>> {
        return repository.searchNotes(searchWord).asLiveData()
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
        isSelectionModeEnabled.value = false
    }

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    fun selectNote(note: Note) {
        if (!selectedNotes.contains(note)) selectedNotes.add(note) else selectedNotes.remove(note)
        isSelectionModeEnabled.value = selectedNotes.size > 0
    }

    fun deleteSelectedNotes(){
        for (note in selectedNotes){
            delete(note)
        }
        isSelectionModeEnabled.value = false
    }

}

class NotesViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}