package zuev.airhockey.network

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player

class NetworkPlayer(hockey: AirHockey) : Player(hockey) {
    init {
        hockey.striker1.addListener {
            sendOtherPlayerPosition(hockey.striker1.x, hockey.striker1.y)
        }
        hockey.tickGenerator.addListener {
            update?.let {
                hockey.striker2.setPosition(it.first, it.second)
                update = null
            }
        }
        TODO("Register server to get update")
    }

    @Volatile
    private var update: Pair<Double, Double>? = null

    private fun sendOtherPlayerPosition(x: Double, y: Double) {
        TODO()
    }
}
