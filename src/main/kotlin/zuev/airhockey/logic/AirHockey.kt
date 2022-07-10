package zuev.airhockey.logic

const val BOARD_WIDTH = 20.0
const val BOARD_HEIGHT = 40.0
const val HOLE_SIZE = 5.0
const val PUCK_SIZE = 2.1
const val STRIKER_SIZE = 2.5

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
                        movePuck()
                    }
                }
            }

            movePuck()
        }
    }

    private fun movePuck() {
        puck.freeMove()
        when (board.hasFallenToHole(puck)) {
            1 -> playerGoal(1)
            2 -> playerGoal(2)
        }
    }

    private fun playerGoal(player: Int) {
        println("Payer $player scored!")
        puck.reset()
    }
}
