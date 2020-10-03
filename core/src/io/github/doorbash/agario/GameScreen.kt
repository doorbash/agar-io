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
import ktx.async.newSingleThreadAsyncContext
import ktx.freetype.registerFreeTypeFontLoaders
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.eagerSingleton
import org.kodein.di.instance
import kotlin.coroutines.CoroutineContext

class GameScreen(
        val game: Game,
        val batch: SpriteBatch = game.batch,
) : KtxScreen {

    val di = DI {
        bind<OrthographicCamera>("game") with instance(OrthographicCamera())
        bind<OrthographicCamera>("gui") with instance(OrthographicCamera())
        bind<Engine>() with instance(PooledEngine())
        bind<SpriteBatch>() with instance(batch)
        bind<CoroutineContext>() with instance(newSingleThreadAsyncContext())
        bind<AssetManager>() with instance(AssetManager().apply { registerFreeTypeFontLoaders() })
        bind<ConnectionManager>() with eagerSingleton { ConnectionManager() }
    }

    val guiCamera by di.instance<OrthographicCamera>("gui")
    val gameCamera by di.instance<OrthographicCamera>("game")
    val context by di.instance<CoroutineContext>()
    val engine by di.instance<Engine>()
    val assets by di.instance<AssetManager>()
    val connectionManager by di.instance<ConnectionManager>()

    init {
        engine.run {
            addSystem(ConnectionCheckIntervalSystem(di))
            addSystem(PingIntervalSystem(di))
            addSystem(InputIntervalSystem(di))
            addSystem(PLayersSystem(di))
            addSystem(FruitsSystem())
            addSystem(RenderSystem(di))
            addSystem(GuiRenderSystem(di))
        }
        connectionManager.init(context, engine)
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