package com.mahmoudrh.roomxml.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.data.Note
import com.mahmoudrh.roomxml.utils.DateFormatter

class NotesAdapter(
    val notesList: ArrayList<Note>,
    val clicked: ClickListener

) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {


    class NotesHolder(item: View) : RecyclerView.ViewHolder(item) {
        val title: TextView
        val content: TextView
        val date: TextView
        val card: CardView
        init {
            title = item.findViewById(R.id.tv_title)
            content = item.findViewById(R.id.tv_content)
            date = item.findViewById(R.id.tv_date)
            card = item.findViewById(R.id.note_card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)
        return NotesHolder(view)
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.title.text = notesList[position].title
        holder.content.text = notesList[position].content
        holder.date.text = DateFormatter.formatDate(notesList[position].date)


        holder.card.setOnClickListener {
            clicked.onNoteItemClicked(position)
            it.setBackgroundColor(clicked.setCardBackground(position))
        }
        holder.card.setOnLongClickListener {
            clicked.onNoteItemLongClicked(position)
            it.setBackgroundColor(clicked.setCardBackground(position))
            true
        }
    }


    override fun getItemCount(): Int {
        return notesList.size
    }

    interface ClickListener {
        fun onNoteItemClicked(position: Int)
        //LongClickFunctionality isn't required in the search activity
        fun onNoteItemLongClicked(position: Int){

        }
        fun setCardBackground(position: Int):Int{
            return Color.WHITE
        }
    }


}