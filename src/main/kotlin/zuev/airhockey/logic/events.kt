package zuev.airhockey.logic


fun interface TickListener {
    fun onTick()
}

abstract class TickGenerator {
    private val listeners = mutableListOf<TickListener>()

    @Synchronized
    fun addListener(listener: TickListener) {
        listeners.add(listener)
    }

    @Synchronized
    protected fun onTick() = listeners.forEach(TickListener::onTick)
}

class ManualTickGenerator : TickGenerator() {
    fun tick() = onTick()
}
