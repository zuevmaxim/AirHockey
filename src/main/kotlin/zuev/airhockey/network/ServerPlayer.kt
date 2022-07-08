package zuev.airhockey.network

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.util.concurrent.Executors

class PuckUpdate(val x: Double, val y: Double, val dx: Double, val dy: Double)

class ServerPlayer(hockey: AirHockey, port: Int) : Player(hockey) {
    val threadPool = Executors.newSingleThreadExecutor()
    val server = ServerSocket(port)

    var sendStriker: Pair<Double, Double>? = null
    var sendPuck: PuckUpdate? = null
    var receive: Pair<Double, Double>? = null

    init {
        hockey.striker1.addListener {
            sendStriker = hockey.striker1.x to hockey.striker1.y
        }
        hockey.puck.addListener {
            sendPuck = PuckUpdate(hockey.puck.x, hockey.puck.y, hockey.puck.dx, hockey.puck.dy)
        }
        hockey.tickGenerator.addListener {
            receive?.let { (x, y) ->
                hockey.striker2.setPosition(x, y)
                receive = null
            }
        }

        threadPool.execute {
            val client = server.accept()
            DataInputStream(client.getInputStream()).use { inputStream ->
                DataOutputStream(client.getOutputStream()).use { outputStream ->
                    while (true) {
                        if (inputStream.available() > 8) {
                            val x = inputStream.readDouble()
                            val y = inputStream.readDouble()
                            receive = x to y
                        }
                        sendStriker?.let { (x, y) ->
                            outputStream.writeBoolean(false)
                            outputStream.writeDouble(x)
                            outputStream.writeDouble(y)
                            sendStriker = null
                        }
                        sendPuck?.let { update ->
                            outputStream.writeBoolean(true)
                            outputStream.writeDouble(update.x)
                            outputStream.writeDouble(update.y)
                            outputStream.writeDouble(update.dx)
                            outputStream.writeDouble(update.dy)
                            sendPuck = null
                        }
                    }
                }
            }
        }
    }
}
