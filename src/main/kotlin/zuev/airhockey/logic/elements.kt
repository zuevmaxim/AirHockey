package zuev.airhockey.logic

const val DELTA = 1e-5

interface Player

class Board(
    val height: Double = BOARD_HEIGHT,
    val width: Double = BOARD_WIDTH,
    val holeSize: Double = HOLE_SIZE,
) {
    fun hasFallenToHole(puck: Puck): Int {
        return if (width / 2 - holeSize / 2 <= puck.x - puck.size / 2 && puck.x + puck.size / 2 <= width / 2 + holeSize / 2) {
            if (puck.y - puck.size / 2 < 0.0) {
                1
            } else if (puck.y + puck.size / 2 > height) {
                2
            } else {
                0
            }
        } else {
            0
        }
    }

    fun isFallingToHole(x: Double, y: Double, r: Double): Boolean {
        return width / 2 - holeSize / 2 <= x && x <= width / 2 + holeSize / 2
            && (y - r <= 0.0 || y + r >= height)
    }
}

fun interface StateListener {
    fun onStateChange()
}

abstract class State {
    private val listeners = mutableListOf<StateListener>()
    fun addListener(listener: StateListener) = listeners.add(listener)
    protected fun stateChanged() = listeners.forEach(StateListener::onStateChange)
}
