package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

class LocalPlayer(hockey: AirHockey, window: MainWindow) : Player(hockey) {
    init {
        val scale = ModelToViewScale.getInstance(hockey)
        window.pane.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {}
            override fun mouseMoved(e: MouseEvent) {
                val newX = scale.viewToModel(e.x)
                val newY = scale.viewToModel(e.y)
                if (newY > hockey.board.height / 2) {
                    hockey.striker1.setPosition(newX, newY)
                }
            }
        })
    }
}
