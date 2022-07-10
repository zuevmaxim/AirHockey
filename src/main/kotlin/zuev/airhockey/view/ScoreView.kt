package zuev.airhockey.view

import zuev.airhockey.logic.Score
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class ScoreView(score: Score) : JPanel() {
    val label1 = JLabel()
    val label2 = JLabel()
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        listOf(label1, label2).forEach {
            it.font = it.font.deriveFont(20.0f)
        }
        updateText(score)

        add(label2)
        add(label1)

        score.addListener {
            updateText(score)
        }
    }

    private fun updateText(score: Score) {
        label1.text = score.player1Score.toString()
        label2.text = score.player2Score.toString()
    }
}
