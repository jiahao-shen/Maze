package com.sam.maze

import com.orhanobut.logger.Logger
import java.util.*

/**
 * Created by sam on 2018/4/26.
 */
class AStar(list: ArrayList<Data>, private var level: Int) {

    class Node(newX: Int, newY: Int): Comparable<Node> {
        var x = newX
        var y = newY
        var value = 0
        var depth = 0
        var father: Node? = null

        fun calculate(goal: Node) {
            value = depth + Math.abs(x - goal.x) + Math.abs(y - goal.y)
        }

        override fun compareTo(other: Node): Int {
            return value - other.value
        }
    }

    private fun isEqual(a: Node, b: Node): Boolean {
        if (a.x == b.x && a.y == b.y)
            return true
        return false
    }

    private val maze = Array(level, { Array(level) { _ -> Type.EMPTY }})

    private val direction = arrayOf(     //上下左右
            intArrayOf(-1, 0),
            intArrayOf(1, 0),
            intArrayOf(0, -1),
            intArrayOf(0, 1)
    )

    private lateinit var start: Node
    private lateinit var goal: Node

    private val openSet = PriorityQueue<Node>()
    private val closedSet = PriorityQueue<Node>()

    private val openVisited = Array(level, { Array<Node?>(level) { _ -> null }})
    private val closedVisited = Array(level, { Array<Node?>(level) { _ -> null }})

    val path = ArrayList<Int>()

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
        start.calculate(goal)       //计算起点的value
        openSet.add(start)          //添加到openSet

        while (!openSet.isEmpty()) {        //openSet非空
            val minNode = openSet.poll()        //取出头节点
            if (isEqual(minNode, goal)) {       //终点则break
                goal = minNode
                break
            }
            for (i in 0..3) {       //遍历上下左右方向
                val x = minNode.x + direction[i][0]
                val y = minNode.y + direction[i][1]
                if (x in 0..(level - 1) && y in 0..(level - 1) && maze[x][y] != Type.WALL) {      //在边界内且不是墙
                    val node = Node(x, y)
                    node.depth = minNode.depth + 1
                    node.calculate(goal)

                    var flag1 = false
                    var flag2 = false

                    if (openVisited[x][y] != null) {
                        flag1 = true
                        if (node.value < openVisited[x][y]!!.value) {
                            openSet.remove(openVisited[x][y])
                            node.father = minNode
                            openSet.add(node)
                            openVisited[x][y] = node
                        }
                    }

                    if (!flag1) {           //不再openSet中
                        if (closedVisited[x][y] != null) {
                            flag2 = true
                        }
                    }

                    if (!flag1 && !flag2) {     //两个表都不在
                        node.father = minNode       //设置父节点
                        openSet.add(node)           //加入openSet
                        openVisited[x][y] = node
                    }
                }
            }
            closedSet.add(minNode)      //放入closedSet
            closedVisited[minNode.x][minNode.y] = minNode
        }

        var node = goal     //从终点开始回溯
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
        path.reverse()      //路径逆序
    }

}