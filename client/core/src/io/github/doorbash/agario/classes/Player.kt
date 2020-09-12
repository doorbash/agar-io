//
// THIS FILE HAS BEEN GENERATED AUTOMATICALLY
// DO NOT CHANGE IT MANUALLY UNLESS YOU KNOW WHAT YOU'RE DOING
// 
// GENERATED USING @colyseus/schema 0.4.41
// 
package io.github.doorbash.agario.classes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import io.colyseus.annotations.SchemaClass
import io.colyseus.annotations.SchemaField
import io.colyseus.serializer.schema.Schema

@SchemaClass
class Player : Schema() {
    @SchemaField("0/float32")
    var x = 0f

    @SchemaField("1/float32")
    var y = 0f

    @SchemaField("2/float32")
    var radius = 0f

    @SchemaField("3/int32")
    var color = 0
    var position = Vector2()
    var _color: Color? = null
    var _strokeColor: Color? = null
}