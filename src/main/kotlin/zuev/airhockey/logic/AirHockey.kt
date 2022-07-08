package zuev.airhockey.logic

class AirHockey(val tickGenerator: TickGenerator) {
    val board = Board()
    val puck = Puck(board)
    val striker1 = Striker(board, isLower = true)
    val striker2 = Striker(board, isLower = false)
    private val strikers = listOf(striker1, striker2)

    init {
        tickGenerator.addListener {
            for (striker in strikers) {
                striker.move()
                if (puck.isBump(striker)) {
                    puck.bump(striker)
                    while (puck.isBump(striker)) {
                        puck.freeMove()
                    }
                }
            }

            puck.freeMove()
        }
    }
}
