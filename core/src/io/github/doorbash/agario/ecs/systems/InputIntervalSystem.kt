package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.logger
import org.kodein.di.DI
import org.kodein.di.instance

private val LOG = logger<InputIntervalSystem>()
private const val INTERVAL = 0.2f; // seconds

class InputIntervalSystem(
        di: DI
) : IntervalSystem(INTERVAL) {

    private val camera by di.instance<Camera>("game")
    private val connectionManager by di.instance<ConnectionManager>()

    override fun updateInterval() {
        if (connectionManager.room == null) return

        val dx = Gdx.input.x - camera.viewportWidth / 2
        val dy = Gdx.input.y - camera.viewportHeight / 2

        connectionManager.sendAngle(Math.toDegrees(Math.atan2(-dy.toDouble(), dx.toDouble())).toInt())
    }
}
