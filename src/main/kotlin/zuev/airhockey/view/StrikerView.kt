package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Striker
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent

class StrikerView(val hockey: AirHockey, val striker: Striker) : JComponent() {
    private val scale = ModelToViewScale.getInstance(hockey)
    var ringColor = Color.BLACK

    init {
        striker.addListener {
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val d = striker.size

        val x = scale.modelToView(striker.x - d / 2)
        val y = scale.modelToView(striker.y - d / 2)
        val diam = scale.modelToView(d)
        g.fillOval(x, y, diam, diam)
        if (ringColor != Color.BLACK) {
            g.color = ringColor
            (g as Graphics2D).apply {
                stroke = BasicStroke(2.0f)
            }
            g.drawOval(x, y, diam, diam)
        }
    }
}
