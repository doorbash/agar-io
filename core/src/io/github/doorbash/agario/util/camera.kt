package io.github.doorbash.agario.util

import com.badlogic.gdx.graphics.Camera
import ktx.graphics.update

fun Camera.update(width: Int, height: Int) {
    update {
        viewportWidth = width.toFloat()
        viewportHeight = height.toFloat()
    }
}