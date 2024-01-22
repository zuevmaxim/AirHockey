package zuev.airhockey.ai

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import java.io.BufferedReader
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class AIPlayer(private val hockey: AirHockey, val isFirstPlayer: Boolean, args: String = "") : Player {
    private var lastScore: Pair<Int, Int>
    private val lock = ReentrantLock()

    val inFile = File(File("."), "in_${if (isFirstPlayer) "1" else "2"}.txt").apply { createNewFile() }
    val outFile = File(File("."), "out_${if (isFirstPlayer) "1" else "2"}.txt").apply { createNewFile() }

    val reader: BufferedReader = outFile.bufferedReader()
    val writer = inFile.writer()
    val process = ProcessBuilder(
        "python3", "AIServer/main.py", inFile.canonicalPath, outFile.canonicalPath,
        *args.split(" ").toTypedArray()
    )
        .redirectError(File("error${if (isFirstPlayer) "1" else "2"}.txt"))
        .redirectOutput(File("output${if (isFirstPlayer) "1" else "2"}.txt"))
        .start()

    val striker = if (isFirstPlayer) hockey.striker1 else hockey.striker2
    val otherStriker = if (isFirstPlayer) hockey.striker2 else hockey.striker1

    var counter = 0

    init {
        hockey.tickGenerator.addListener { action() }
        lastScore = extractScore()
        hockey.score.addListener {
            lastScore = extractScore().also { newScore ->
                onEndSession(newScore.first > lastScore.first == isFirstPlayer)
            }
        }
    }

    fun save(path: String) {
        write("save $path\n")
        process.waitFor()
    }

    private fun write(s: String) = lock.withLock {
        writer.run {
            write(s)
            flush()
        }
    }

    private fun onEndSession(success: Boolean) {
        write(if (success) "success\n" else "fail\n")
    }

    private fun extractScore() = hockey.score.run { player1Score to player2Score }

    private fun extractState() = doubleArrayOf(
        hockey.puck.x, hockey.puck.y, hockey.puck.dx, hockey.puck.dy,
        striker.x, striker.y, striker.dx, striker.dy,
        otherStriker.x, otherStriker.y, otherStriker.dx, otherStriker.dy,
    )

    private fun action() {
        write(extractState().joinToString(" ", postfix = "\n"))
        var s: String?
        do {
            s = reader.readLine()
        } while (s == null)
        val (x, y) = s.split(" ").map(String::toDouble)
        striker.setPosition(striker.x + x, striker.y + y)
    }
}
