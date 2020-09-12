package io.github.doorbash.agario

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