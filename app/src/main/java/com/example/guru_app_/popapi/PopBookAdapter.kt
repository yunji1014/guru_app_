package com.example.guru_app_.popapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_app_.R

class PopBookAdapter(private val popbook: List<PopBooks>) : RecyclerView.Adapter<PopBookAdapter.BookViewHolder>() {
    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.bookTitle)
        val authorTextView: TextView = view.findViewById(R.id.bookAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pop_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = popbook[position]
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
    }

    override fun getItemCount() = popbook.size
}