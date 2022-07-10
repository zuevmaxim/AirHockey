package zuev.airhockey.view

import zuev.airhockey.ai.RandomPlayer
import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import zuev.airhockey.logic.TickGenerator
import zuev.airhockey.network.ClientPlayer
import zuev.airhockey.network.ServerPlayer
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.OverlayLayout
import javax.swing.Timer

class ModelToViewScale(val hockey: AirHockey, val window: JFrame) {
    private val scale: Double get() = (window.height - 100) / hockey.board.height
    fun modelToView(x: Double) = (x * scale).toInt()
    fun viewToModel(x: Int) = x / scale

    companion object {
        fun getInstance(hockey: AirHockey) = Services.getService(hockey, ModelToViewScale::class)
    }
}

class MainWindow(hockey: AirHockey) : JFrame() {
    val scale = ModelToViewScale(hockey, this).also { Services.addService(hockey, it) }
    val pane = JPanel().apply {
        setLocation(20, 20)
        layout = OverlayLayout(this)
    }.also { add(it) }

    init {
        setSize(400, 800)
        title = "AirHockey"
        defaultCloseOperation = EXIT_ON_CLOSE
    }
    val board = BoardView(hockey).also { pane.add(it) }
    val striker1 = StrikerView(hockey, hockey.striker1).also { pane.add(it) }
    val striker2 = StrikerView(hockey, hockey.striker2).also { pane.add(it) }
    val puck = PuckView(hockey).also { pane.add(it) }
    val score = ScoreView(hockey.score).also { pane.add(it) }
}

class TimeTickGenerator : TickGenerator() {
    private val ticker = Timer(10) { onTick() }.apply { start() }
}

var player1: Player? = null
var player2: Player? = null

fun main(args: Array<String>) {
    val hockey = AirHockey(TimeTickGenerator())
    val window = MainWindow(hockey)
    window.isVisible = true

    if (args.isEmpty()) {
        player1 = LocalPlayer(hockey, window, hockey.striker1)
        player2 = RandomPlayer(hockey)
    } else if (args[0] == "server") {
        player1 = LocalPlayer(hockey, window, hockey.striker1)
        player2 = ServerPlayer(hockey, args[1].toInt())
    } else if (args[0] == "client") {
        player1 = ClientPlayer(hockey, args[1], args[2].toInt())
        player2 = LocalPlayer(hockey, window, hockey.striker2)
    }

}
