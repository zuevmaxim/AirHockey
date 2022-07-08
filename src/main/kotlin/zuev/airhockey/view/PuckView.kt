package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent

class PuckView(val hockey: AirHockey) : JComponent() {
    private val scale = ModelToViewScale.getInstance(hockey)

    init {
        hockey.puck.addListener {
            repaint()
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val puck = hockey.puck
        val d = puck.size
        g.color = Color.RED
        g.fillOval(
            scale.modelToView(puck.x - d / 2),
            scale.modelToView(puck.y - d / 2),
            scale.modelToView(d),
            scale.modelToView(d)
        )
    }
}
