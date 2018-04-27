package com.sam.maze.algorithm

import com.sam.maze.custom.Data
import com.sam.maze.custom.Type
import java.util.*

class BFS(list: ArrayList<Data>, private val level: Int) {

    class Node(val x: Int, val y: Int) {
        var father: Node? = null

    }

    private fun isEqual(a: Node, b: Node): Boolean {
        if (a.x == b.x && a.y == b.y)
            return true
        return false
    }

    private val maze = Array(level, { Array(level) { _ -> Type.EMPTY }})
    private val visited = Array(level, { Array(level) { _ -> false }})

    private val direction = arrayOf(     //上下左右
            intArrayOf(-1, 0),
            intArrayOf(1, 0),
            intArrayOf(0, -1),
            intArrayOf(0, 1)
    )

    private lateinit var start: Node
    private lateinit var goal: Node

    val path = ArrayList<Int>()
    private val queue = LinkedList<Node>()

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
                    visited[i][j] = true
                }
                if (maze[i][j] == Type.GOAL) {
                    goal = Node(i, j)       //终点
                }
            }
        }

        queue.push(start)
        while (!queue.isEmpty()) {
            val node = queue.pollFirst()
            if (isEqual(node, goal))  {
                goal = node
                break
            }
            for (i in 0..3) {
                val x = node.x + direction[i][0]
                val y = node.y + direction[i][1]
                if (x in 0..(level - 1) && y in 0..(level - 1) && maze[x][y] != Type.WALL && !visited[x][y]) {
                    val newNode = Node(x, y)
                    newNode.father = node
                    queue.add(newNode)
                    visited[x][y] = true
                }
            }
        }

        var node = goal
        while (true) {
            if (node.father != null) {
                for (i in 0..3) {
                    if (node.father!!.x + direction[i][0] == node.x && node.father!!.y + direction[i][1] == node.y) {
                        path.add(i)
                        break
                    }
                }
                node = node.father!!
            } else {
                break
            }
        }
        path.reverse()
    }
}