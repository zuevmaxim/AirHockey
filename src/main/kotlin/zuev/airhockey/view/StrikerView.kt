package zuev.airhockey.view

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Striker
import java.awt.Graphics
import javax.swing.JComponent

class StrikerView(val hockey: AirHockey, val striker: Striker) : JComponent() {
    private val scale = ModelToViewScale.getInstance(hockey)

    init {
        striker.addListener {
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val d = striker.size

        g.fillOval(
            scale.modelToView(striker.x - d / 2),
            scale.modelToView(striker.y - d / 2),
            scale.modelToView(d),
            scale.modelToView(d)
        )
    }
}
