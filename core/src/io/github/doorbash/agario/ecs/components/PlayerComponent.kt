package io.github.doorbash.agario.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.doorbash.agario.classes.Player
import ktx.ashley.mapperFor

class PlayerComponent : Component, Pool.Poolable {
    var player: Player? = null
    var clientId: String? = null

    override fun reset() {
        player = null
        clientId = null
    }

    companion object {
        var mapper = mapperFor<PlayerComponent>()
    }
}