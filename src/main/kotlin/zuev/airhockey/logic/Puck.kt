package zuev.airhockey.logic

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

private const val MU = 0.999

class Puck(
    val board: Board,
    val size: Double = PUCK_SIZE,
) : State() {

    @Volatile
    var x: Double = 0.0
        private set

    @Volatile
    var y: Double = 0.0
        private set

    @Volatile
    var dx: Double = 0.0
        private set

    @Volatile
    var dy: Double = 0.0
        private set

    init {
        reset()
    }

    fun reset() {
        x = board.width / 2
        y = board.height / 2
        dx = Random.nextDouble(0.1, 0.2) * if (Random.nextBoolean()) 1 else -1
        dy = Random.nextDouble(0.1, 0.2) * if (Random.nextBoolean()) 1 else -1
        stateChanged()
    }

    fun speed(): Double = sqrt(dx.pow(2) + dy.pow(2))

    tailrec fun freeMove() {
        val r = size / 2
        val newX = x + dx
        val newY = y + dy
        val isFalling = board.isFallingToHole(newX, newY, r)
        if (isFalling && board.checkEdgeBump(this)) {
            freeMove()
            return
        }
        if (!isFalling && (newX < r || newX > board.width - r)) {
            dx *= -1
            freeMove()
        } else if (!isFalling && (newY < r || newY > board.height - r)) {
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

    private fun Board.checkEdgeBump(puck: Puck): Boolean {
        return checkEdgeBump(puck, width / 2 - holeSize / 2, 0.0)
            || checkEdgeBump(puck, width / 2 + holeSize / 2, 0.0)
            || checkEdgeBump(puck, width / 2 - holeSize / 2, height)
            || checkEdgeBump(puck, width / 2 + holeSize / 2, height)
    }

    private fun checkEdgeBump(puck: Puck, x: Double, y: Double): Boolean {
        val newX = puck.x + puck.dx
        val newY = puck.y + puck.dy
        return if ((newX - x).pow(2) + (newY- y).pow(2) <= (puck.size / 2).pow(2)) {
            val edge = Point(x, y)
            val pck = Point(puck.x, puck.y)
            val pckV = Point(puck.dx, puck.dy)

            val pckVProj = findNormal(pck, edge, pckV)

            val pckVAdd = Point(pckV.x - pckVProj.x, pckV.y - pckVProj.y)
            val pckVAxis = Point(pckVProj.x - pck.x, pckVProj.y - pck.y)

            puck.dx = pckVAdd.x - pckVAxis.x
            puck.dy = pckVAdd.y - pckVAxis.y
            true
        } else {
            false
        }
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
