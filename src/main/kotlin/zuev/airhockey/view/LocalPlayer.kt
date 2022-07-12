package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import zuev.airhockey.logic.Striker
import java.awt.Color
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

class LocalPlayer(hockey: AirHockey, window: MainWindow, striker: Striker) : Player(hockey) {
    init {
        val scale = ModelToViewScale.getInstance(hockey)
        val isPlayer1 = striker === hockey.striker1
        (if (isPlayer1) window.striker1 else window.striker2).ringColor = Color.BLUE
        window.pane.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {}
            override fun mouseMoved(e: MouseEvent) {
                val newX = scale.viewToModel(e.x)
                val newY = scale.viewToModel(e.y)
                if (isPlayer1 == newY > hockey.board.height / 2) {
                    striker.setPosition(newX, newY)
                }
            }
        })
    }
}
