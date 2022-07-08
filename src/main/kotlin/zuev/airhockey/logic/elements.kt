package zuev.airhockey.logic

const val DELTA = 1e-5

abstract class Player(val hockey: AirHockey)

class Board(
    val height: Double = 100.0,
    val width: Double = 50.0,
    val holeSize: Double = 10.0,
)

fun interface StateListener {
    fun onStateChange()
}

abstract class State {
    private val listeners = mutableListOf<StateListener>()
    fun addListener(listener: StateListener) = listeners.add(listener)
    protected fun stateChanged() = listeners.forEach(StateListener::onStateChange)
}
