package io.github.doorbash.agario.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.doorbash.agario.classes.Fruit
import ktx.ashley.mapperFor

class FruitComponent : Component, Pool.Poolable {
    var fruit: Fruit? = null

    override fun reset() {
        fruit = null
    }

    companion object {
        var mapper = mapperFor<FruitComponent>()
    }
}