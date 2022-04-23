package com.mahmoudrh.roomxml.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahmoudrh.roomxml.NotesViewModel
import com.mahmoudrh.roomxml.NotesViewModelFactory
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.adapters.NotesAdapter
import com.mahmoudrh.roomxml.application.NotesApplication
import com.mahmoudrh.roomxml.data.Note

class SearchResultsActivity : AppCompatActivity(),NotesAdapter.ClickListener {

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((application as NotesApplication).repository)
    }
    private var query: String? = ""
    var resultsList: ArrayList<Note> = arrayListOf()

    lateinit var resultsRecycler: RecyclerView
    lateinit var notesAdapter: NotesAdapter
    lateinit var noResults:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        resultsRecycler = findViewById(R.id.results_rv)
        noResults = findViewById(R.id.no_results_tv)

        searchTheDB(intent)
        notesAdapter = NotesAdapter(resultsList, this)
        initiateRecyclerView()
    }
    //when clicking search while the user is in the results activity (launchMode = singleTop)
    override fun onNewIntent(intent: Intent) {
        Log.e("Mah ", "onNewIntent is Called")
        super.onNewIntent(intent)
        searchTheDB(intent)
    }

    private fun initiateRecyclerView() {
        resultsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultsRecycler.adapter = notesAdapter
    }

    private fun searchTheDB(intent: Intent) {
        query = handleIntent(intent)
        if (query != null) {
            Log.e("Mah ", "The search word is ($query)")
            notesViewModel.searchNotes(query!!).observe(this) { notes ->
                if (resultsList.size > 0) {
                    resultsList.clear()
                }
                if (notes != null) {
                    resultsList.addAll(notes)
                }
                notesAdapter.notifyDataSetChanged()
                setNoResultsVisibility(resultsList.size)
            }
        }
    }

    private fun setNoResultsVisibility(numOfResults: Int) {
        noResults.visibility = if (numOfResults >0) View.GONE else View.VISIBLE
    }

    //Get the query.
    private fun handleIntent(intent: Intent):String? {
        if (intent.action == Intent.ACTION_SEARCH) {
//            query = intent.getStringExtra(SearchManager.QUERY)
            return  intent.getStringExtra(SearchManager.QUERY)
        }
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_main_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchItem.expandActionView()
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean { return false }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                finish()
                return false
            }
        })
        searchView.setQuery(query, false)
        searchView.clearFocus()
        return true
    }

    override fun onNoteItemClicked(position: Int) {
        val i = Intent(this, NoteActivity::class.java)
        val noteObj = resultsList[position]
        i.putExtra("noteObj", noteObj)
        startActivity(i)
    }

}