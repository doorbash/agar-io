package io.github.doorbash.agario.util

import com.badlogic.gdx.graphics.Color

fun Color.toInt(): Int {
    return ((255 * r).toInt() shl 24) or ((255 * g).toInt() shl 16) or ((255 * b).toInt() shl 8) or ((255 * a).toInt())
}