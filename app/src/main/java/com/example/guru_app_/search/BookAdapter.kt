package com.example.guru_app_.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guru_app_.shelf.BookShelfActivity
import com.example.guru_app_.R
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.models.Book

class BookAdapter(
    private val context: Context,
    private val books: MutableList<Book>,
    private val bookDao: BookDao
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.imageView)
        val bookTitle: TextView = view.findViewById(R.id.textTitle)
        val bookAuthor: TextView = view.findViewById(R.id.textAuthor)
        val bookISBN: TextView = view.findViewById(R.id.bookISBN)
        val bookPublisher: TextView = view.findViewById(R.id.bookPublisher)
        val addBookButton: Button = view.findViewById(R.id.AddBookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bookTitle.text = book.title
        holder.bookAuthor.text = book.author
        holder.bookISBN.text = "ISBN: ${book.isbn}"
        holder.bookPublisher.text = "출판사: ${book.publisher}"

        if (book.coverImage != null) {
            Glide.with(holder.bookImage.context).load(book.coverImage).into(holder.bookImage)
        } else {
            holder.bookImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.addBookButton.setOnClickListener {
            bookDao.addBook(book)  // 책 데이터를 데이터베이스에 저장

            val intent = Intent(context, BookShelfActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
}
