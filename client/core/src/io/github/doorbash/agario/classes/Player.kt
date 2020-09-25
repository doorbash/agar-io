package io.github.doorbash.agario.classes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import io.colyseus.default

class Player {
    var x = Float.default

    var y = Float.default

    var radius = Float.default

    var color = Int.default

    var position = Vector2()
    var _color: Color? = null
    var _strokeColor: Color? = null
}