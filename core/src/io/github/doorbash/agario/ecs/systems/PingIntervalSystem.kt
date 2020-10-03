package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.logger

private val LOG = logger<PingIntervalSystem>()
private const val INTERVAL = 5f; // seconds

class PingIntervalSystem(
        val connectionManager: ConnectionManager
) : IntervalSystem(INTERVAL) {

    override fun updateInterval() {
        if (connectionManager.room == null) return

        connectionManager.sendPing()
    }
}
