package com.sam.maze

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.maze.MainActivity.Status.*
import com.sam.maze.algorithm.AStar
import com.sam.maze.algorithm.BFS
import com.sam.maze.algorithm.IDAStar
import com.sam.maze.custom.Data
import com.sam.maze.custom.DataAdapter
import com.sam.maze.custom.Method
import com.sam.maze.custom.Type
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity() {

    private var start: Data? = null
    private var goal: Data? = null
    private var status = EMPTY
    private var flag = true
    private var level = 10
    private var path = ArrayList<Int>()
    private var time = 0L
    private var solveThread: Thread? = null

    private lateinit var mapList: ArrayList<Data>
    private lateinit var mapAdapter: DataAdapter
    private lateinit var mapLayoutManager: GridLayoutManager

    enum class Status {
        WALL, EMPTY, START, FINAL
    }

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

        emptyButton.setOnClickListener {
            status = EMPTY
        }

        wallButton.setOnClickListener {
            status = WALL
        }

        startButton.setOnClickListener {
            status = START
        }

        goalButton.setOnClickListener {
            status = FINAL
        }

        aStarButton.setOnClickListener {
            solve(Method.ASTAR)
        }

        bfsButton.setOnClickListener {
            solve(Method.BFS)
        }

        idaStarButton.setOnClickListener {
            solve(Method.IDASTAR)
        }

        clearPathButton.setOnClickListener {
            clearPath()
        }

        levelBar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
                initMap()
            }

            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
            }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                level = progress
                levelText.text = "Level:$level"
            }

        }

        stopButton.setOnClickListener {
            solveThread?.interrupt()
            progressBar.visibility = View.INVISIBLE
        }

    }

    private fun solve(method: Method) {
        if (start == null || goal == null) {
            toast("请先确定起点和终点!")
        } else {
            clearPath()
            flag = false
            solveThread = Thread(Runnable {
                if (!hasPath()) {
                    toast("不存在路径")
                } else {
                    runOnUiThread {
                        progressBar.visibility = View.VISIBLE
                    }
                    val t1 = System.currentTimeMillis()
                    path = when (method) {
                        Method.ASTAR -> {
                            val aStar = AStar(mapList, level)
                            aStar.path
                        }
                        Method.BFS -> {
                            val bfs = BFS(mapList, level)
                            bfs.path
                        }
                        Method.IDASTAR -> {
                            val idaStar = IDAStar(mapList, level)
                            idaStar.path
                        }
                    }
                    val t2 = System.currentTimeMillis()
                    time = t2 - t1
                    showPath()
                }
            })
            solveThread?.start()
        }
    }

    private fun hasPath(): Boolean {
        val aStar = AStar(mapList, level)
        if (aStar.path.size == 0)
            return false
        return true
    }

    private fun clearPath() {
        for ((index, item) in mapList.withIndex()) {
            if (item.type == Type.PATH) {
                item.type = Type.EMPTY
                mapAdapter.notifyItemChanged(index)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showPath() {
        runOnUiThread {
            timeView.text = "Time:${time}ms"
            stepsView.text = "Steps:${path.size}"
            var index = mapList.indexOf(start)
            for (i in 0 until (path.size - 1)) {        //上左右下
                when (path[i]) {
                    0 -> {
                        index -= level
                    }
                    1 -> {
                        index--
                    }
                    2 -> {
                        index++
                    }
                    3 -> {
                        index += level
                    }
                }
                mapList[index].type = Type.PATH
                mapAdapter.notifyItemChanged(index)
            }
            progressBar.visibility = View.INVISIBLE
        }

    }

    private fun initMap() {
        flag = true
        status = START
        start = null
        goal = null
        mapList = ArrayList()
        for (i in 0 until level * level) {
            val random = Random()
            when (random.nextInt(10)) {
                0, 1, 2, 3, 4, 5, 6 -> mapList.add(Data(Type.EMPTY))
                7, 8, 9 -> mapList.add(Data(Type.WALL))
            }
        }
        mapAdapter = DataAdapter(mapList, SizeUtils.dp2px(700 - level * 2f) / level)
        mapLayoutManager = GridLayoutManager(this, level)
        mapView.layoutManager = mapLayoutManager
        mapView.adapter = mapAdapter
        mapAdapter.setOnItemClickListener { adapter, _, position ->
            if (flag) {
                val item = adapter.getItem(position) as Data    //获取当前点击对象
                when(status) {
                    START -> {      //并且是确定起点
                        mapAdapter.notifyItemChanged(mapList.indexOf(start))
                        item.type = Type.START
                        start?.type = Type.EMPTY
                        start = item
                    }
                    FINAL ->{
                        mapAdapter.notifyItemChanged(mapList.indexOf(goal))
                        item.type = Type.GOAL
                        goal?.type = Type.EMPTY
                        goal = item
                    }
                    WALL -> {
                        item.type = Type.WALL
                    }
                    EMPTY -> {
                        item.type = Type.EMPTY
                    }
                }
                mapAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImmersionBar.with(this).destroy()
    }
}
