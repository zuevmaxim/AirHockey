package zuev.airhockey.ai

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import zuev.airhockey.logic.Striker
import kotlin.random.Random

class RandomPlayer(hockey: AirHockey, striker: Striker) : Player {
    init {
        hockey.tickGenerator.addListener {
            val deltaX = Random.nextDouble(-1.0, 1.0)
            val deltaY = Random.nextDouble(-1.0, 1.0)
            striker.setPosition(striker.x + deltaX, striker.y + deltaY)
        }
    }
}
