package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import java.awt.Graphics
import javax.swing.JComponent

class BoardView(val hockey: AirHockey) : JComponent() {
    private val scale = ModelToViewScale.getInstance(hockey)

    override fun paint(g: Graphics) {
        super.paint(g)
        g.drawRect(0, 0, scale.modelToView(hockey.board.width), scale.modelToView(hockey.board.height))
    }
}
