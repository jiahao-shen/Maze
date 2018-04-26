package com.sam.maze

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.orhanobut.logger.Logger

/**
 * Created by sam on 2018/4/26.
 */
class DataAdapter(list: ArrayList<Data>, var height: Int) : BaseQuickAdapter<Data, BaseViewHolder>(R.layout.item_data, list) {

    override fun convert(helper: BaseViewHolder, item: Data) {
        when (item.type) {
            Type.EMPTY -> {
                helper.setBackgroundColor(R.id.item_view, android.graphics.Color.WHITE)
            }
            Type.WALL -> {
                helper.setBackgroundColor(R.id.item_view, android.graphics.Color.BLACK)
            }
            Type.START -> {
                helper.setBackgroundColor(R.id.item_view, android.graphics.Color.RED)
                        .setText(R.id.item_view, "S")
            }
            Type.GOAL -> {
                helper.setBackgroundColor(R.id.item_view, android.graphics.Color.GREEN)
                        .setText(R.id.item_view, "G")
            }
            Type.PATH -> {
                helper.setBackgroundColor(R.id.item_view, android.graphics.Color.YELLOW)
            }
        }
        helper.getView<TextView>(R.id.item_view).height = height
        helper.getView<TextView>(R.id.item_view).textSize = height * 0.5f

    }
}