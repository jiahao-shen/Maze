package com.sam.maze.algorithm

import android.os.Build
import android.support.annotation.RequiresApi
import com.orhanobut.logger.Logger
import com.sam.maze.custom.Data
import com.sam.maze.custom.Type
import java.lang.Integer.min

class IDAStar(list: ArrayList<Data>, private var level: Int) {

    class Node(newX: Int, newY: Int) {
        var x = newX
        var y = newY
        var value = 0
        var depth = 0

        fun calculate(goal: Node) {
            value = depth + Math.abs(x - goal.x) + Math.abs(y - goal.y)
        }

    }

    private fun isEqual(a: Node, b: Node) = a.x == b.x && a.y == b.y

    private val maze = Array(level, { Array(level) { _ -> Type.EMPTY }})

    private val direction = arrayOf(     //上左右下
            intArrayOf(-1, 0),
            intArrayOf(0, -1),
            intArrayOf(0, 1),
            intArrayOf(1, 0)
    )

    private var limit = 0
    private var minValue = Int.MAX_VALUE
    private var flag = false

    private lateinit var start: Node
    private lateinit var goal: Node

    val path = ArrayList<Int>()
    private val temp = Array(level * level, { _ -> -1 })

    init {
        for ((index, value) in list.withIndex()) {
            maze[index / level][index % level] = value.type
        }
        solvePath()
    }

    private fun solvePath() {
        for (i in 0 until level) {
            for (j in 0 until level) {
                if (maze[i][j] == Type.START) {
                    start = Node(i, j)      //起点
                }
                if (maze[i][j] == Type.GOAL) {
                    goal = Node(i, j)       //终点
                }
            }
        }
        start.calculate(goal)
        limit = start.value
        Logger.i("first:$limit")

        do {
            minValue = Int.MAX_VALUE
            dfs(start, 0)
            limit = minValue
            Logger.i("$limit")
        } while(!flag)

        Logger.d(path)
    }

    private fun dfs(node: Node, preMove: Int) {
        if (isEqual(node, goal)) {
            flag = true
            for (item in temp) {
                if (item == -1)
                    break
                else
                    path.add(item)
            }
            return
        }
        for (i in 0..3) {
            if (i + preMove == 3 && node.depth > 0)
                continue
            val x = node.x + direction[i][0]
            val y = node.y + direction[i][1]
            if (x in (0..(level - 1)) && y in 0..(level - 1) && maze[x][y] != Type.WALL) {
                val newNode = Node(x, y)
                newNode.depth = node.depth + 1
                newNode.calculate(goal)

                if (newNode.value <= limit) {
                    temp[node.depth] = i
                    dfs(newNode, i)
                    if (flag)
                        return
                } else {
                    minValue = Math.min(minValue, newNode.value)
                }
            }
        }
    }
}