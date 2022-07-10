package zuev.airhockey.logic

import kotlin.math.*

private const val MAX_STRIKER_SPEED = 0.1

class Striker(
    val board: Board,
    val size: Double = STRIKER_SIZE,
    val isLower: Boolean,
) : State() {
    @Volatile
    var x: Double = board.width / 2
        private set

    @Volatile
    var y: Double = board.height / 4 + if (isLower) board.height / 2 else 0.0
        private set

    @Volatile
    var dx: Double = 0.0
        private set

    @Volatile
    var dy: Double = 0.0
        private set

    @Volatile
    private var reqX: Double = 0.0

    @Volatile
    private var reqY: Double = 0.0

    fun setPosition(newX: Double, newY: Double) {
        reqX = newX
        reqY = newY
    }

    fun move() {
        val r = size / 2
        val newX = max(r, min(board.width - r, reqX))
        val h = board.height / 2
        val newY = if (isLower) {
            max(r + h, min(h + h - r, reqY))
        } else {
            max(r, min(h - r, reqY))
        }
        dx = newX - x
        dy = newY - y

        val curSpeed = sqrt(dx.pow(2) + dy.pow(2))
        if (curSpeed > MAX_STRIKER_SPEED) {
            dx *= MAX_STRIKER_SPEED / curSpeed
            dy *= MAX_STRIKER_SPEED / curSpeed
        }


        if (abs(x - newX) > DELTA || abs(y - newY) > DELTA) {
            x = newX
            y = newY
            stateChanged()
        }
    }
}
