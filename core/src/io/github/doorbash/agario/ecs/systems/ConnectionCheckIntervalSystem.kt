package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.debug
import ktx.log.logger

private val LOG = logger<ConnectionCheckIntervalSystem>()
private const val INTERVAL = 3f; // seconds

class ConnectionCheckIntervalSystem(
        val connectionManager: ConnectionManager
) : IntervalSystem(INTERVAL) {

    override fun updateInterval() {
        LOG.debug { "Checking connection..." }
        connectionManager.connectIfNeeded()
    }
}