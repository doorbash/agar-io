package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.logger
import org.kodein.di.DI
import org.kodein.di.instance

private val LOG = logger<PingIntervalSystem>()
private const val INTERVAL = 5f; // seconds

class PingIntervalSystem(
        di: DI
) : IntervalSystem(INTERVAL) {

    private val connectionManager by di.instance<ConnectionManager>()

    override fun updateInterval() {
        if (connectionManager.room == null) return

        connectionManager.sendPing()
    }
}
