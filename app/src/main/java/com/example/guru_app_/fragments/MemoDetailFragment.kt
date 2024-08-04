package com.example.guru_app_.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.guru_app_.R
import com.example.guru_app_.database.MemoDao
import com.example.guru_app_.models.Memo

class MemoDetailFragment : Fragment() {//메모 상세 정보 표시 프래그먼트
    private var memoId: Int = -1
    private lateinit var memoDao: MemoDao
    private lateinit var memoTitle: EditText
    private lateinit var memoContent: EditText
    private lateinit var addCameraImageButton: ImageButton
    private lateinit var addGalleryImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            memoId = it.getInt(ARG_MEMO_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_memo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memoDao = MemoDao(requireContext())

        memoTitle = view.findViewById(R.id.memo_title)
        memoContent = view.findViewById(R.id.memo_content)
        addCameraImageButton = view.findViewById(R.id.add_camera_image_button)
        addGalleryImageButton = view.findViewById(R.id.add_gallery_image_button)

        if (memoId != -1) {
            val memo: Memo? = memoDao.getMemoById(memoId)
            memo?.let {
                memoTitle.setText(it.title)
                memoContent.setText(it.content)
            }
        }

        // TODO: Add functionality for camera and gallery image buttons
    }

    companion object {
        private const val ARG_MEMO_ID = "memo_id"

        @JvmStatic
        fun newInstance(memoId: Int) =
            MemoDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MEMO_ID, memoId)
                }
            }
    }
}
