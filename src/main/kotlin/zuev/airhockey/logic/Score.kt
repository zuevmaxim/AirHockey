package zuev.airhockey.logic

class Score : State() {
    @Volatile
    var player1Score = 0
        private set
    @Volatile
    var player2Score = 0
        private set

    fun goal(player: Int) {
        when (player) {
            1 -> {
                player1Score++
                stateChanged()
            }
            2 -> {
                player2Score++
                stateChanged()
            }
            else -> error("Unknown player $player")
        }
    }
}
