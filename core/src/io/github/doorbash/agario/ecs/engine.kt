package io.github.doorbash.agario.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import io.github.doorbash.agario.classes.Fruit
import io.github.doorbash.agario.classes.Player
import io.github.doorbash.agario.ecs.components.CircleComponent
import io.github.doorbash.agario.ecs.components.FruitComponent
import io.github.doorbash.agario.ecs.components.PlayerComponent
import io.github.doorbash.agario.helpers.FRUIT_RADIUS
import io.github.doorbash.agario.helpers.PLAYERS_STROKE_COLOR_MUL
import io.github.doorbash.agario.util.toInt
import ktx.ashley.entity
import ktx.ashley.with

fun Engine.createPlayer(player: Player, clientId: String) {
    player.entity = entity {
        with<CircleComponent> {
            position.set(player.x, player.y)
            color = player.color
            strokeColor = Color(player.color).mul(PLAYERS_STROKE_COLOR_MUL).toInt()
            radius = player.radius
        }
        with<PlayerComponent> {
            this.player = player
            this.clientId = clientId
        }
    }
}

fun Engine.createFruit(fruit: Fruit) {
    fruit.entity = entity {
        with<CircleComponent> {
            position.set(fruit.x, fruit.y)
            color = fruit.color
            radius = FRUIT_RADIUS
        }
        with<FruitComponent> { this.fruit = fruit }
    }
}