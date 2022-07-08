package zuev.airhockey.network

import zuev.airhockey.logic.AirHockey
import zuev.airhockey.logic.Player
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.concurrent.Executors

class ClientPlayer(hockey: AirHockey, address: String, port: Int) : Player(hockey) {
    val threadPool = Executors.newSingleThreadExecutor()
    val socket = Socket(address, port)

    var sendStriker: Pair<Double, Double>? = null
    var receivePuck: PuckUpdate? = null
    var receiveStriker: Pair<Double, Double>? = null

    init {
        hockey.striker2.addListener {
            sendStriker = hockey.striker2.x to hockey.striker2.y
        }
        hockey.tickGenerator.addListener {
            receiveStriker?.let { (x, y) ->
                hockey.striker1.setPosition(x, y)
                receiveStriker = null
            }
            receivePuck?.let { update ->
                hockey.puck.force(update.x, update.y, update.dx, update.dy)
                receivePuck = null
            }
        }

        threadPool.execute {
            DataInputStream(socket.getInputStream()).use { inputStream ->
                DataOutputStream(socket.getOutputStream()).use { outputStream ->
                    while (true) {
                        if (inputStream.available() > 0) {
                            val isPuck = inputStream.readBoolean()
                            if (isPuck) {
                                val x = inputStream.readDouble()
                                val y = inputStream.readDouble()
                                val dx = inputStream.readDouble()
                                val dy = inputStream.readDouble()
                                receivePuck = PuckUpdate(x, y, dx, dy)
                            } else {
                                val x = inputStream.readDouble()
                                val y = inputStream.readDouble()
                                receiveStriker = x to y
                            }
                        }
                        sendStriker?.let { (x, y) ->
                            outputStream.writeDouble(x)
                            outputStream.writeDouble(y)
                            sendStriker = null
                        }
                    }
                }
            }
        }
    }
}
