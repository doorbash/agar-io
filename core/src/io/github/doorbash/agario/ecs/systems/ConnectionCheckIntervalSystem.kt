package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.debug
import ktx.log.logger
import org.kodein.di.DI
import org.kodein.di.instance

private val LOG = logger<ConnectionCheckIntervalSystem>()
private const val INTERVAL = 3f; // seconds

class ConnectionCheckIntervalSystem(
        di: DI
) : IntervalSystem(INTERVAL) {

    private val connectionManager by di.instance<ConnectionManager>()

    override fun updateInterval() {
        LOG.debug { "Checking connection..." }
        connectionManager.connectIfNeeded()
    }
}