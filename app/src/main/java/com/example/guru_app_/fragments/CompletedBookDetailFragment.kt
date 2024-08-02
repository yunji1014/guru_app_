package com.example.guru_app_.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.guru_app_.R
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.models.Book

class CompletedBookDetailFragment : Fragment() {

    private var bookId: Int = -1
    private lateinit var bookDao: BookDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getInt(ARG_BOOK_ID)
        }
        bookDao = BookDao(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_completed_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val book: Book? = bookDao.getBookById(bookId)
        book?.let {
            view.findViewById<TextView>(R.id.book_title).text = "도서명: ${it.title}"
            view.findViewById<TextView>(R.id.book_author).text = "지은이: ${it.author}"
            view.findViewById<TextView>(R.id.start_date).text = "Start Date: ${it.startDate}"
            view.findViewById<TextView>(R.id.end_date).text = "End Date: ${it.endDate}"
            view.findViewById<RatingBar>(R.id.book_rating).rating = it.rating ?: 0f
        }
    }

    companion object {
        private const val ARG_BOOK_ID = "book_id"

        @JvmStatic
        fun newInstance(bookId: Int) =
            CompletedBookDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_BOOK_ID, bookId)
                }
            }
    }
}
