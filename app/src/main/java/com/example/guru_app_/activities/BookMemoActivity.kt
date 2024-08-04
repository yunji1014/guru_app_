package com.example.guru_app_.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RatingBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.guru_app_.R
import com.example.guru_app_.database.BookDao
import com.example.guru_app_.database.MemoDao
import com.example.guru_app_.fragments.CompletedBookDetailFragment
import com.example.guru_app_.fragments.MemoListFragment
import com.example.guru_app_.fragments.ReadingBookDetailFragment

class BookMemoActivity : AppCompatActivity(), MemoListFragment.MemoItemClickListener {

    private lateinit var memoDao: MemoDao
    private lateinit var bookDao: BookDao
    private var bookId: Int = -1
    //메모 상세보기 액티비티 시작, 결과 받아오는 런쳐
    private val memoDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val memoListFragment = supportFragmentManager.findFragmentById(R.id.memo_list_fragment_container) as? MemoListFragment
            memoListFragment?.refreshMemoList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_memo)
        // DAO 초기화
        bookDao = BookDao(this)
        memoDao = MemoDao(this)
        // 인텐트로부터 책 ID를 받아오기
        bookId = intent.getIntExtra("BOOK_ID", -1)
        if (bookId == -1) {
            finish()
            return
        }
        //// 책 정보를 가져옴
        val book = bookDao.getBookById(bookId)
        // 프래그먼트를 동적으로 추가

        if (savedInstanceState == null) {
            if (book?.status == "completed") {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.book_detail_fragment_container, CompletedBookDetailFragment.newInstance(bookId))
                    .commit()
            } else {
                val readingFragment = ReadingBookDetailFragment.newInstance(bookId)
                readingFragment.setCompleteButtonClickListener {
                    showRatingDialog()
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.book_detail_fragment_container, readingFragment)
                    .commit()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.memo_list_fragment_container, MemoListFragment.newInstance(bookId))
                .commit()
        }
        // 메모 추가 버튼 클릭 리스너
        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            val intent = Intent(this, MemoDetailActivity::class.java)
            intent.putExtra("MEMO_ID", -1)
            intent.putExtra("BOOK_ID", bookId)
            memoDetailLauncher.launch(intent)
        }

        // 뒤로가기 버튼 클릭 리스너 추가
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }
    // 메모 아이템 클릭 시 호출되는 함수
    override fun onMemoItemClick(memoId: Int) {
        val intent = Intent(this, MemoDetailActivity::class.java)
        intent.putExtra("MEMO_ID", memoId)
        intent.putExtra("BOOK_ID", bookId)
        memoDetailLauncher.launch(intent)
    }
    // 책 완료 후 평가 다이얼로그를 보여주는 함수
    private fun showRatingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rating, null)
        val ratingBar: RatingBar = dialogView.findViewById(R.id.rating_bar)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Rate this book")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating
                bookDao.updateBookRating(bookId, rating)
                bookDao.updateBookStatus(bookId, "completed")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.book_detail_fragment_container, CompletedBookDetailFragment.newInstance(bookId))
                    .commit()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}
