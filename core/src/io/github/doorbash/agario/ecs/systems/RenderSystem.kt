package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.doorbash.agario.ecs.components.CircleComponent
import io.github.doorbash.agario.ecs.components.FruitComponent
import io.github.doorbash.agario.ecs.components.PlayerComponent
import io.github.doorbash.agario.helpers.PLAYERS_STROKE_THICKNESS
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import ktx.math.vec3
import org.kodein.di.DI
import org.kodein.di.instance

private val LOG = logger<RenderSystem>()

class RenderSystem(
        di: DI,
) : IteratingSystem(allOf(CircleComponent::class).get()) {

    private val camera by di.instance<OrthographicCamera>("game")

    private var shapeRenderer: ShapeRenderer? = null
    private var cameraPosition = vec3()
    private var middleOfScrren = vec3()

    override fun addedToEngine(engine: Engine?) {
        LOG.debug { "creating shapeRenderer" }
        shapeRenderer = ShapeRenderer()
        super.addedToEngine(engine)
    }

    override fun removedFromEngine(engine: Engine?) {
        LOG.debug { "destroying shapeRenderer" }
        shapeRenderer?.dispose()
        super.removedFromEngine(engine)
    }

    override fun update(deltaTime: Float) {
        middleOfScrren.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
        cameraPosition = camera.unproject(middleOfScrren)
        shapeRenderer?.projectionMatrix = camera.combined
        shapeRenderer?.use(ShapeRenderer.ShapeType.Filled) {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val circle = entity[CircleComponent.mapper]
        require(circle != null) { "Entity |entity| must have a CircleComponent. entity=$entity" }

        if (!circleIsInViewport(circle)) {
            return
        }

        with(shapeRenderer!!) {
            if (entity.has(PlayerComponent.mapper)) {
                color.set(circle.strokeColor)
                circle(circle.position.x, circle.position.y, circle.radius)
                color.set(circle.color)
                circle(circle.position.x, circle.position.y, circle.radius - PLAYERS_STROKE_THICKNESS)
            } else if (entity.has(FruitComponent.mapper)) {
                color.set(circle.color)
                circle(circle.position.x, circle.position.y, circle.radius)
            }
        }
    }

    private fun circleIsInViewport(circle: CircleComponent): Boolean {
        return when {
            circle.position.x + circle.radius < cameraPosition.x - camera.zoom * camera.viewportWidth / 2 -> false
            circle.position.x - circle.radius > cameraPosition.x + camera.zoom * camera.viewportWidth / 2 -> false
            circle.position.y + circle.radius < cameraPosition.y - camera.zoom * camera.viewportHeight / 2 -> false
            circle.position.y - circle.radius > cameraPosition.y + camera.zoom * camera.viewportHeight / 2 -> false
            else -> true
        }
    }
}
