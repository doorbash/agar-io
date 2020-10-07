package io.github.doorbash.agario

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.doorbash.agario.ecs.systems.*
import io.github.doorbash.agario.helpers.ConnectionManager
import io.github.doorbash.agario.util.update
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.freetype.async.registerFreeTypeFontLoaders
import org.kodein.di.*

class GameScreen(
        val game: Game,
        val batch: SpriteBatch = game.batch,
) : KtxScreen {

    val di = DI {
        bind<OrthographicCamera>("game") with singleton { OrthographicCamera() }
        bind<OrthographicCamera>("gui") with singleton { OrthographicCamera() }
        bind<Engine>() with singleton { PooledEngine() }
        bind<SpriteBatch>() with singleton { batch }
        bind<AssetStorage>() with singleton { AssetStorage().apply { KtxAsync.initiate(); registerFreeTypeFontLoaders() } }
        bind<ConnectionManager>() with singleton { ConnectionManager(instance()) }
    }
    val did = di.direct

    val guiCamera by di.instance<OrthographicCamera>("gui")
    val gameCamera by di.instance<OrthographicCamera>("game")
    val engine by di.instance<Engine>()
    val assets by di.instance<AssetStorage>()
    val connectionManager by di.instance<ConnectionManager>()

    init {
        engine.run {
            addSystem(ConnectionCheckIntervalSystem(did.instance()))
            addSystem(PingIntervalSystem(did.instance()))
            addSystem(InputIntervalSystem(did.instance("game"), did.instance()))
            addSystem(PLayersSystem(did.instance("game"), did.instance()))
            addSystem(FruitsSystem())
            addSystem(RenderSystem(did.instance("game")))
            addSystem(GuiRenderSystem(did.instance("gui"), did.instance(), did.instance(), did.instance()))
        }
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
        engine.systems.forEach { engine.removeSystem(it) }
        engine.removeAllEntities()
        connectionManager.dispose()
    }

}