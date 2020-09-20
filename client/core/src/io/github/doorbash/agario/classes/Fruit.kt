package io.github.doorbash.agario.classes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import io.colyseus.annotations.SchemaField
import io.colyseus.default
import io.colyseus.serializer.schema.Schema

class Fruit : Schema() {
    @SchemaField("0/string")
    var key = String.default

    @SchemaField("1/float32")
    var x = Float.default

    @SchemaField("2/float32")
    var y = Float.default

    @SchemaField("3/int32")
    var color = Int.default

    var position = Vector2()
    var _color: Color? = null
}