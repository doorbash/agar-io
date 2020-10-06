package io.github.doorbash.agario.classes

import com.badlogic.ashley.core.Entity
import io.colyseus.annotations.SchemaField
import io.colyseus.util.default
import io.colyseus.serializer.schema.Schema

class Player : Schema() {
    @SchemaField("0/float32")
    var x = Float.default

    @SchemaField("1/float32")
    var y = Float.default

    @SchemaField("2/float32")
    var radius = Float.default

    @SchemaField("3/int32")
    var color = Int.default

    var entity: Entity? = null
}