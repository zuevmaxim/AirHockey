package zuev.airhockey.logic

import zuev.airhockey.ai.AIPlayer
import kotlin.math.roundToInt

var player1: Player? = null
var player2: Player? = null

fun main() {
    val tickGenerator = ManualTickGenerator()
    val hockey = AirHockey(tickGenerator)
    player1 = AIPlayer(hockey, true)
    player2 = AIPlayer(hockey, false, "random")
    var games = 0
    hockey.score.addListener {
        games++
        val win = hockey.score.player1Score.toDouble() / games
        val add = hockey.score.player1Score - hockey.score.player2Score
        println("$games ${(win * 100).roundToInt() / 100.0} ${if (add > 0) "+" else ""}$add")
    }
    val totalGames = 100_000
    while (games < totalGames) {
        tickGenerator.tick()
    }
    (player1 as AIPlayer).save("player1")
}