package com.mahmoudrh.roomxml.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mahmoudrh.roomxml.NotesViewModel
import com.mahmoudrh.roomxml.NotesViewModelFactory
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.adapters.NotesAdapter
import com.mahmoudrh.roomxml.application.NotesApplication
import com.mahmoudrh.roomxml.data.Note

class MainActivity : AppCompatActivity(), NotesAdapter.ClickListener {


    lateinit var notesRecycler: RecyclerView
    lateinit var fabAdd: FloatingActionButton
    lateinit var removeSelectedBtn: Button
    lateinit var removeAllBtn: Button
    lateinit var notesAdapter: NotesAdapter
    var notesList: ArrayList<Note> = arrayListOf()

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((application as NotesApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesRecycler = findViewById(R.id.notes_rv)
        fabAdd = findViewById(R.id.fab_add)
        removeSelectedBtn = findViewById(R.id.remove_selected_btn)
        removeAllBtn = findViewById(R.id.remove_all_btn)

        notesViewModel.isSelectionModeEnabled.observe(this){
            removeSelectedBtn.visibility = if (it) View.VISIBLE else View.GONE
            removeAllBtn.visibility = if (it) View.VISIBLE else View.GONE
        }

        loadNotesFromDB()
        notesAdapter = NotesAdapter(notesList, this)

        initiateRecyclerView()

        fabAdd.setOnClickListener {
            addNote()
        }
        removeSelectedBtn.setOnClickListener {
            notesViewModel.deleteSelectedNotes()
        }
        removeAllBtn.setOnClickListener {
            notesViewModel.deleteAll()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.search_main_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.app_bar_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    menu.findItem(R.id.app_bar_search).collapseActionView()
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        return true
    }

    private fun addNote() {
        val i = Intent(this, NoteActivity::class.java)
        startActivity(i)
    }

    private fun loadNotesFromDB() {
        notesViewModel.allNotes.observe(this, object : Observer<List<Note>> {
            override fun onChanged(notes: List<Note>?) {
                if (notesList.size > 0) {
                    notesList.clear()
                }
                if (notes != null) {
                    notesList.addAll(notes)
                }
                notesAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initiateRecyclerView() {
        notesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        notesRecycler.adapter = notesAdapter
        setRecyclerViewItemTouchListener()
    }

    //-> Adding Swipe-to-Delete functionality to the notesRecycler.
    private fun setRecyclerViewItemTouchListener() {
        val itemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val noteObject = notesList[position]
                notesViewModel.delete(noteObject)
                undoDeleteSnackBar(noteObject)
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(notesRecycler)
    }

    //-> Showing SnackBar with Action UNDO to re-insert the deleted note object which is passed as a parameter.
    private fun undoDeleteSnackBar(noteItem: Note) {
        Snackbar.make(
            findViewById(R.id.main_activity_root),
            "Note Deleted",
            Snackbar.LENGTH_SHORT
        ).setAction("Undo") {
            notesViewModel.insert(noteItem)
        }.show()
    }


    override fun onNoteItemClicked(position: Int) {
        if (notesViewModel.isSelectionModeEnabled.value!!){
            notesViewModel.selectNote(notesList[position])
        }else{
            val i = Intent(this, NoteActivity::class.java)
            val noteObj = notesList[position]
            i.putExtra("noteObj", noteObj)
            getResult.launch(i)
        }
    }

    override fun onNoteItemLongClicked(position: Int) {
        Log.e("Mah ", "Long Click detected " )
        notesViewModel.selectNote(notesList[position])
//        notesViewModel.enableSelectionMode()

    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val deletedNote = it.data?.getParcelableExtra<Note>(NoteActivity.NOTE_DELETED_EXTRA)
                if (deletedNote != null) {
                    undoDeleteSnackBar(deletedNote)
                }
            }
        }

    override fun setCardBackground(position: Int): Int {
        if (notesViewModel.selectedNotes.contains(notesList[position])){
            return Color.LTGRAY
        }
        return super.setCardBackground(position)
    }

}


