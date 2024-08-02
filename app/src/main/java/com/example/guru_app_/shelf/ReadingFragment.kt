package com.example.guru_app_.shelf

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_app_.R
import com.example.guru_app_.database.BookDao

class ReadingFragment : Fragment() {
    private lateinit var bookImageAdapter: BookImageAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookDao = BookDao(requireContext())

        recyclerView = view.findViewById(R.id.recyclerView)
        val gridLayoutManager = GridLayoutManager(context, 3) // 열의 수
        recyclerView.layoutManager = gridLayoutManager

        val books = bookDao.getAllBooks().filter { it.status == "reading" }

        bookImageAdapter = BookImageAdapter(requireContext(), books, bookDao)
        recyclerView.adapter = bookImageAdapter
    }

    @SuppressLint("DetachAndAttachSameFragment")
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    private fun loadBooks() {
        val bookDao = BookDao(requireContext())
        val books = bookDao.getAllBooks() // 예시로 모든 책을 불러오는 메서드
        bookImageAdapter.updateBooks(books)
    }

    // 데이터 갱신 메서드
    fun refreshBooks() {
        loadBooks()
    }
}

