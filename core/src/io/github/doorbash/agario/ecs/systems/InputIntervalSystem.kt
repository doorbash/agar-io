package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.logger

private val LOG = logger<InputIntervalSystem>()
private const val INTERVAL = 0.2f; // seconds

class InputIntervalSystem(
        val camera: OrthographicCamera,
        val connectionManager: ConnectionManager
) : IntervalSystem(INTERVAL) {

    override fun updateInterval() {
        if (connectionManager.room == null) return

        val dx = Gdx.input.x - camera.viewportWidth / 2
        val dy = Gdx.input.y - camera.viewportHeight / 2

        connectionManager.sendAngle(Math.toDegrees(Math.atan2(-dy.toDouble(), dx.toDouble())).toInt())
    }
}
