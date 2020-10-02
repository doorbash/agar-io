package io.github.doorbash.agario.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2

class CircleComponent : Component, Pool.Poolable {
    val position = vec2()
    var radius = 0f
    var color = 0
    var strokeColor = 0

    override fun reset() {
        position.set(Vector2.Zero)
        radius = 0f
        color = 0
        strokeColor = 0
    }

    companion object {
        val mapper = mapperFor<CircleComponent>()
    }
}
