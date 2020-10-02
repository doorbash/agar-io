package io.github.doorbash.agario.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.doorbash.agario.Game
import io.github.doorbash.agario.ecs.systems.*
import io.github.doorbash.agario.helpers.ConnectionManager
import io.github.doorbash.agario.util.update
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.newSingleThreadAsyncContext
import ktx.freetype.registerFreeTypeFontLoaders

class GameScreen(
        val game: Game,
        val batch: SpriteBatch = game.batch,
) : KtxScreen {

    val gameCamera = OrthographicCamera()
    val guiCamera = OrthographicCamera()
    val engine by lazy {
        val context = newSingleThreadAsyncContext()
        PooledEngine().apply {
            addSystem(ConnectionCheckIntervalSystem())
            addSystem(PingIntervalSystem())
            addSystem(InputIntervalSystem(gameCamera))
            addSystem(PLayersSystem(gameCamera))
            addSystem(FruitsSystem())
            addSystem(RenderSystem(gameCamera))
            addSystem(GuiRenderSystem(batch, guiCamera, assets))
            ConnectionManager.init(context, this)
        }
    }
    val assets: AssetManager by lazy {
        AssetManager().apply { registerFreeTypeFontLoaders() }
    }

    override fun render(delta: Float) {
        clearScreen(0.98f, 0.99f, 1f, 1f)
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        gameCamera.update(width, height)
        guiCamera.update(width, height)
    }

    override fun dispose() {
        assets.dispose()
        engine.removeAllEntities()
        ConnectionManager.dispose()
    }

}