package com.mahmoudrh.roomxml.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.mahmoudrh.roomxml.application.NotesApplication
import com.mahmoudrh.roomxml.NotesViewModel
import com.mahmoudrh.roomxml.NotesViewModelFactory

import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.data.Note
import java.util.*

class NoteActivity : AppCompatActivity() {

    lateinit var titleTextField: TextInputLayout
    lateinit var contentTextField: TextInputLayout

    lateinit var editSaveBtn: MenuItem
    lateinit var saveNewNoteBtn: MenuItem

    var isEditModeEnabled = true
    var noteObj: Note? = null

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((application as NotesApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        titleTextField = findViewById(R.id.et_title)
        contentTextField = findViewById(R.id.et_content)
        noteObj = intent.getParcelableExtra("noteObj")

        if (noteObj != null) {
            setUpEditMode()


        } else {
            setUpAdditionMode()


        }
    }

    private fun setUpEditMode(){

        titleTextField.editText?.setText(noteObj!!.title)
        contentTextField.editText?.setText(noteObj!!.content)

        titleTextField.editText?.doOnTextChanged { _, _, _, _ ->
            editSaveBtn.isEnabled = true
        }
        contentTextField.editText?.doOnTextChanged { _, _, _, _ ->
            editSaveBtn.isEnabled = true
        }
    }

    private fun setUpAdditionMode(){
        titleTextField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0)
                saveNewNoteBtn.isVisible = true
        }
        contentTextField.editText?.doOnTextChanged { _, _, _, count ->
            if (count > 0)
                saveNewNoteBtn.isVisible = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (noteObj != null) {
            menuInflater.inflate(R.menu.edit_note_menu, menu)
            editSaveBtn = menu.findItem(R.id.edit_save_btn)
        } else {
            menuInflater.inflate(R.menu.new_note_menu, menu)
            saveNewNoteBtn = menu.findItem(R.id.save_new_note_btn)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_save_btn) {
            enableEditMode()
            if (isEditModeEnabled) {
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_save)
            } else {
                if (checkInputs()) {
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_edit)
                    saveChanges()
                } else {
                    Toast.makeText(this, "Note Must Have Title & Content", Toast.LENGTH_SHORT)
                        .show()
                    enableEditMode()
                }

            }
        } else if (item.itemId == R.id.delete_btn) {
            deleteNote()
        } else if (item.itemId == R.id.save_new_note_btn) {
            val titleText = titleTextField.editText?.text.toString()
            val contentText = contentTextField.editText?.text.toString()
            if (checkInputs()) {
                val newNote =
                    Note(title = titleText, content = contentText, date = Date().time.toString())
                notesViewModel.insert(newNote)
                finish()
            } else
                Toast.makeText(this, "Note Must Have Title & Content", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun checkInputs(): Boolean {
        return (!titleTextField.editText?.text.isNullOrEmpty() && !contentTextField.editText?.text.isNullOrEmpty())
    }

    private fun deleteNote() {
        noteObj?.let {
            val deleteIntent = Intent()
            deleteIntent.putExtra(NOTE_DELETED_EXTRA, noteObj)
            setResult(Activity.RESULT_OK, deleteIntent)
            notesViewModel.delete(it)
            finish()
        }
    }

    private fun saveChanges() {
        val titleText = titleTextField.editText?.text.toString()
        val contentText = contentTextField.editText?.text.toString()

        //check if the note actually changed before updating the database
        if (titleText != noteObj!!.title || contentText != noteObj!!.content) {
            val newNote = Note(
                id = noteObj?.id ?: 0,
                title = titleText,
                content = contentText,
                date = Date().time.toString()
            )
            notesViewModel.update(newNote)
        }
    }

    private fun enableEditMode() {
        isEditModeEnabled = !isEditModeEnabled
        titleTextField.isEnabled = !titleTextField.isEnabled
        contentTextField.isEnabled = !contentTextField.isEnabled
    }

    companion object {
        const val NOTE_DELETED_EXTRA = "NoteActivity.NOTE_DELETED"
    }
}
