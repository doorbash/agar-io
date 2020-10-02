package io.github.doorbash.agario.classes

import io.colyseus.annotations.SchemaField
import io.colyseus.serializer.schema.Schema
import io.colyseus.serializer.schema.types.MapSchema

class GameState : Schema() {
    @SchemaField("0/map/ref", Player::class)
    var players = MapSchema(Player::class.java)

    @SchemaField("1/map/ref", Fruit::class)
    var fruits = MapSchema(Fruit::class.java)
}