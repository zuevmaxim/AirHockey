package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent

class BoardView(val hockey: AirHockey) : JComponent() {
    private val scale = ModelToViewScale.getInstance(hockey)

    override fun paint(g: Graphics) {
        super.paint(g)
        g.drawRect(0, 0, scale.modelToView(hockey.board.width), scale.modelToView(hockey.board.height))
        g.color = Color.BLUE
        val height = 6
        g.fillRect(scale.modelToView(hockey.board.width / 2 - hockey.board.holeSize / 2), -height / 2, scale.modelToView(hockey.board.holeSize ), height)
        g.fillRect(scale.modelToView(hockey.board.width / 2 - hockey.board.holeSize / 2), scale.modelToView(hockey.board.height ) - height / 2, scale.modelToView(hockey.board.holeSize ), height)
    }
}
