package com.sam.maze

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.SeekBar
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity() {

    private var start: Data? = null
    private var goal: Data? = null
    private var isStart = true
    private var flag = true
    private var level = 10

    private lateinit var mapList: ArrayList<Data>
    private lateinit var mapAdapter: DataAdapter
    private lateinit var mapLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.addLogAdapter(AndroidLogAdapter())
        ImmersionBar.with(this).transparentBar().init()
        Utils.init(application)

        initMap()

        initButton.setOnClickListener {
            initMap()
        }

        startButton.setOnClickListener {
            isStart = true
        }

        goalButton.setOnClickListener {
            isStart = false
        }

        aStarButton.setOnClickListener {
            if (start == null || goal == null) {
                toast("请先确定起点和终点!")
            } else {
                flag = false
                val aStar = AStar(mapList, level)
                val path = aStar.path
                val time = aStar.runTime
                showPath(path, time)
            }
        }

        levelBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                initMap()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                level = progress / 5 + 10
                levelText.text = "Level:$level"
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun showPath(path: ArrayList<Int>, time: Long) {
        timeView.text = "Time:${time}ms"
        if (path.size == 0) {
            toast("不存在路径!")
            stepsView.text = "Steps:+∞"
        } else {
            stepsView.text = "Steps:${path.size}"
            var index = mapList.indexOf(start)
            for (i in 0 until (path.size - 1)) {
                when (path[i]) {
                    0 -> {
                        index -= level
                    }
                    1 -> {
                        index += level
                    }
                    2 -> {
                        index--
                    }
                    3 -> {
                        index++
                    }
                }
                mapList[index].type = Type.PATH
                mapAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun initMap() {
        flag = true
        isStart = true
        mapList = ArrayList()
        for (i in 0 until level * level) {
            val random = Random()
            when (random.nextInt(4)) {
                0, 1, 2 -> mapList.add(Data(Type.EMPTY))
                3 -> mapList.add(Data(Type.WALL))
            }
        }
        mapAdapter = DataAdapter(mapList, SizeUtils.dp2px(700 - level * 2f) / level)
        mapLayoutManager = GridLayoutManager(this, level)
        mapView.layoutManager = mapLayoutManager as RecyclerView.LayoutManager?
        mapView.adapter = mapAdapter
        mapAdapter.setOnItemClickListener { adapter, _, position ->
            if (flag) {
                val item = adapter.getItem(position) as Data
                if (item.type == Type.EMPTY) {
                    if (isStart) {
                        item.type = Type.START
                        if (start == null) {
                            start = item
                        } else {
                            start!!.type = Type.EMPTY
                            start = item
                        }
                    } else {
                        item.type = Type.GOAL
                        if (goal == null) {
                            goal = item
                        } else {
                            goal!!.type = Type.EMPTY
                            goal = item
                        }
                    }
                    mapAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImmersionBar.with(this).destroy()
    }
}
