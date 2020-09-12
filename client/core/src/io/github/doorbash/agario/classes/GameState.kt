//
// THIS FILE HAS BEEN GENERATED AUTOMATICALLY
// DO NOT CHANGE IT MANUALLY UNLESS YOU KNOW WHAT YOU'RE DOING
// 
// GENERATED USING @colyseus/schema 0.4.41
// 
package io.github.doorbash.agario.classes

import io.colyseus.annotations.SchemaClass
import io.colyseus.annotations.SchemaField
import io.colyseus.serializer.schema.Schema

@SchemaClass
class GameState : Schema() {
    @SchemaField("0/map/ref")
    var players = MapSchema(Player::class.java)

    @SchemaField("1/map/ref")
    var fruits = MapSchema(Fruit::class.java)
}