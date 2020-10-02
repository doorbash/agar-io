package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.doorbash.agario.ecs.components.CircleComponent
import io.github.doorbash.agario.ecs.components.FruitComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.error
import ktx.log.logger

private val LOG = logger<FruitsSystem>()

class FruitsSystem : IteratingSystem(
        allOf(FruitComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val fruit = entity[FruitComponent.mapper]
        require(fruit != null) { "Entity |entity| must have a FruitComponent. entity=$entity" }
        val circle = entity[CircleComponent.mapper]
        require(circle != null) { "Entity |entity| must have a CircleComponent. entity=$entity" }

        if (fruit.fruit == null) {
            LOG.error { "fruit.fruit is null" }
            return
        }

        with(fruit.fruit!!) {
            circle.position.set(x, y)
        }
    }
}
