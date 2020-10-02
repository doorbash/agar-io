package io.github.doorbash.agario

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.doorbash.agario.screens.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Game : KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()
        KtxAsync.initiate()
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
        super.create()
    }

    override fun dispose() {
        batch.dispose()
        super.dispose()
    }
}