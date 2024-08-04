package com.example.guru_app_.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru_app_.R
import com.example.guru_app_.models.Memo

class MemoListAdapter(private val memoList: List<Memo>) : RecyclerView.Adapter<MemoListAdapter.MemoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo_list, parent, false)
        return MemoViewHolder(view)
    }
    //ViewHolder와 데이터 간의 연결을 설정
    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.title.text = memo.title
        holder.content.text = memo.content
    }

    override fun getItemCount(): Int {//RecyclerView에 표시할 항목의 총 수를 반환
        return memoList.size
    }

    fun getMemoAt(position: Int): Memo {//특정 위치에 있는 메모 객체를 반환
        return memoList[position]
    }
    //개별 메모 항목의 뷰를 관리
    class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.memo_item_title)
        val content: TextView = itemView.findViewById(R.id.memo_item_content)
    }
}
