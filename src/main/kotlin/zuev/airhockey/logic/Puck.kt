package zuev.airhockey.logic

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

private const val MU = 0.999

class Puck(
    val board: Board,
    val size: Double = 5.0,
) : State() {

    @Volatile
    var x: Double = board.width / 2
        private set

    @Volatile
    var y: Double = board.height / 2
        private set

    @Volatile
    var dx: Double = Random.nextDouble(0.2, 0.3) * if (Random.nextBoolean()) 1 else -1
        private set

    @Volatile
    var dy: Double = Random.nextDouble(0.2, 0.3) * if (Random.nextBoolean()) 1 else -1
        private set

    fun speed(): Double = sqrt(dx.pow(2) + dy.pow(2))

    fun freeMove() {
        val r = size / 2
        val newX = x + dx
        val newY = y + dy
        if (newX < r || newX > board.width - r) {
            dx *= -1
            freeMove()
        } else if (newY < r || newY > board.height - r) {
            dy *= -1
            freeMove()
        } else if (abs(x - newX) > DELTA || abs(y - newY) > DELTA) {
            dx *= MU
            dy *= MU
            x = newX
            y = newY
            stateChanged()
        }
    }

    fun isBump(striker: Striker): Boolean {
        return (striker.x - x).pow(2) + (striker.y - y).pow(2) <= (size / 2 + striker.size / 2).pow(2)
    }

    fun bump(striker: Striker) {
        val str = Point(striker.x, striker.y)
        val strV = Point(striker.x + striker.dx, striker.y + striker.dy)
        val pck = Point(x, y)
        val pckV = Point(x + dx, y + dy)

        val strVProj = findNormal(str, pck, strV)
        val pckVProj = findNormal(pck, str, pckV)

        val pckVAdd = Point(pckV.x - pckVProj.x, pckV.y - pckVProj.y)
        val pckVAxis = Point(pckVProj.x - pck.x, pckVProj.y - pck.y)
        val strVAxis = Point(strVProj.x - str.x, strVProj.y - str.y)

        dx = 2 * strVAxis.x - pckVAxis.x + pckVAdd.x
        dy = 2 * strVAxis.y - pckVAxis.y + pckVAdd.y
    }

    fun force(newX: Double, newY: Double, newDx: Double, newDy: Double) {
        x = newX
        y = newY
        dx = newDx
        dy = newDy
        stateChanged()
    }
}

private data class Point(val x: Double, val y: Double)

/*
    Find x.
    B
    |
    X----D
    |   /
    |  /
    | /
    |/
    A
 */
private fun findNormal(a: Point, b: Point, d: Point): Point {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val x = (d.x * abx.pow(2) + a.x * aby.pow(2) - aby * abx * (a.y - d.y)) / (abx.pow(2) + aby.pow(2))
    val y = a.y + aby * (x - a.x) / abx
    return Point(x, y)
}
