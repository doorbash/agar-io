package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import io.github.doorbash.agario.helpers.ConnectionManager
import ktx.log.logger

private val LOG = logger<InputIntervalSystem>()
private const val INTERVAL = 0.2f; // seconds

class InputIntervalSystem(
        private val camera: OrthographicCamera,
) : IntervalSystem(INTERVAL) {
    override fun updateInterval() {
        if (ConnectionManager.room == null) return

        val dx = Gdx.input.x - camera.viewportWidth / 2
        val dy = Gdx.input.y - camera.viewportHeight / 2

        ConnectionManager.sendAngle(Math.toDegrees(Math.atan2(-dy.toDouble(), dx.toDouble())).toInt())
    }
}
