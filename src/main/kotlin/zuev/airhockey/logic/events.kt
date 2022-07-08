package zuev.airhockey.logic


fun interface TickListener {
    fun onTick()
}

abstract class TickGenerator {
    private val listeners = mutableListOf<TickListener>()

    fun addListener(listener: TickListener) {
        listeners.add(listener)
    }

    protected fun onTick() = listeners.forEach(TickListener::onTick)
}

class ManualTickGenerator : TickGenerator() {
    fun tick() = onTick()
}
