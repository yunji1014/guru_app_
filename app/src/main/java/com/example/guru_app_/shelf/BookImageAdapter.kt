package com.example.guru_app_.shelf

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guru_app_.R
import com.example.guru_app_.activities.BookMemoActivity
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.models.Book

class BookImageAdapter(private val context: Context, private var books: List<Book>, private val bookDao: BookDao) : RecyclerView.Adapter<BookImageAdapter.BookImageViewHolder>() {

    class BookImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImageButton: ImageButton = view.findViewById(R.id.bookImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_image, parent, false)
        return BookImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookImageViewHolder, position: Int) {
        val book = books[position]
        val bookDao = BookDao(context) // context 전달 필요

        if (book.coverImage != null) {
            Glide.with(holder.bookImageButton.context).load(book.coverImage).into(holder.bookImageButton)
        } else {
            holder.bookImageButton.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.bookImageButton.apply {
            setOnClickListener {
                val context = it.context
                val bookId = book.id
                val startDate = book.startDate // book 객체에 startDate가 포함되어 있다고 가정합니다.

                // Toast 메시지로 책의 ID와 start_date를 표시합니다.
                Toast.makeText(context, "ID: $bookId, Start Date: $startDate", Toast.LENGTH_SHORT).show()

                // 기존의 Intent 코드
                val intent = Intent(context, BookMemoActivity::class.java).apply {
                    putExtra("BOOK_ID", bookId)
                }
                context.startActivity(intent)
            }

            setOnLongClickListener {
                // 경고창 생성

                AlertDialog.Builder(it.context).apply {
                    setTitle("경고")
                    setMessage("삭제하시겠습니까?")
                    setPositiveButton("예") { dialog, which ->
                        // 삭제 로직을 여기에 추가
                        val bookDao = BookDao(it.context)
                        bookDao.deleteBook(book.id)

                        Toast.makeText(it.context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    setNegativeButton("아니오") { dialog, which ->
                        dialog.dismiss()
                    }
                }.show()

                true // Long click 이벤트 처리를 완료했음을 알림
            }
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }


    // 데이터 갱신 메서드 추가
    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}
