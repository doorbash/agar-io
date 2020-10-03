package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import io.github.doorbash.agario.ecs.components.CircleComponent
import io.github.doorbash.agario.ecs.components.PlayerComponent
import io.github.doorbash.agario.helpers.ConnectionManager
import io.github.doorbash.agario.helpers.OTHER_PLAYERS_LERP
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.error
import ktx.log.logger
import org.kodein.di.DI
import org.kodein.di.instance

private val LOG = logger<PLayersSystem>()

class PLayersSystem(
        di: DI
) : IteratingSystem(
        allOf(PlayerComponent::class).get()
) {

    private val camera by di.instance<OrthographicCamera>("game")
    private val connectionManager by di.instance<ConnectionManager>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }
        val circle = entity[CircleComponent.mapper]
        require(circle != null) { "Entity |entity| must have a CircleComponent. entity=$entity" }

        if (player.player == null) {
            LOG.error { "player.player is null" }
            return
        }

        if (player.clientId == null) {
            LOG.error { "player.clientId is null" }
            return
        }

        with(player.player!!) {
            circle.radius = radius
            if (player.clientId == connectionManager.sessionId) {
                circle.position.set(
                        MathUtils.lerp(circle.position.x, x, connectionManager.lerp
                        ), MathUtils.lerp(circle.position.y, y, connectionManager.lerp))

                camera.position.x = circle.position.x
                camera.position.y = circle.position.y
                camera.zoom = circle.radius / 60f
                camera.update()

//                LOG.debug { "updating camera position (${camera.position.x}, ${camera.position.y})" }

            } else circle.position.set(
                    MathUtils.lerp(circle.position.x, x, OTHER_PLAYERS_LERP),
                    MathUtils.lerp(circle.position.y, y, OTHER_PLAYERS_LERP)
            )
        }
    }
}
