package io.github.doorbash.agario

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Game : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
        super.create()
    }

    override fun dispose() {
        super.dispose()
    }
}