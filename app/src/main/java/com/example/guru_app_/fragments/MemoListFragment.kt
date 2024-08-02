package com.example.guru_app_.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_app_.R
import com.example.guru_app_.adapters.MemoListAdapter
import com.example.guru_app_.adapters.RecyclerItemClickListener
import com.example.guru_app_.database.MemoDao
import com.example.guru_app_.models.Memo

class MemoListFragment : Fragment() {

    private lateinit var memoListAdapter: MemoListAdapter
    private lateinit var memoDao: MemoDao
    private var listener: MemoItemClickListener? = null
    private var bookId: Int = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MemoItemClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MemoItemClickListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bookId = it.getInt(ARG_BOOK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_memo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.memo_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        memoDao = MemoDao(requireContext())
        refreshMemoList()

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(requireContext(), recyclerView, object :
                RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val memo = memoListAdapter.getMemoAt(position)
                    listener?.onMemoItemClick(memo.id ?: -1)
                }

                override fun onLongItemClick(view: View, position: Int) {
                    val memo = memoListAdapter.getMemoAt(position)
                    showDeleteConfirmationDialog(memo)
                }
            })
        )
    }

    private fun showDeleteConfirmationDialog(memo: Memo) {
        AlertDialog.Builder(context)
            .setMessage("메모를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                memoDao.deleteMemo(memo.id!!)
                refreshMemoList()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    fun refreshMemoList() {
        val memos = memoDao.getMemosForBook(bookId)
        memoListAdapter = MemoListAdapter(memos)
        view?.findViewById<RecyclerView>(R.id.memo_recycler_view)?.adapter = memoListAdapter
    }

    interface MemoItemClickListener {
        fun onMemoItemClick(memoId: Int)
    }

    companion object {
        private const val ARG_BOOK_ID = "book_id"

        @JvmStatic
        fun newInstance(bookId: Int) =
            MemoListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_BOOK_ID, bookId)
                }
            }
    }
}
