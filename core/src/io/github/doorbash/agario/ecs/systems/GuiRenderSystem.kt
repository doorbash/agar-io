package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.doorbash.agario.helpers.ConnectionManager
import io.github.doorbash.agario.helpers.PATH_FONT_NOTO
import ktx.assets.getValue
import ktx.freetype.loadFreeTypeFont
import ktx.graphics.use
import ktx.log.logger
import org.kodein.di.DI
import org.kodein.di.instance

private val LOG = logger<RenderSystem>()

class GuiRenderSystem(
        di: DI
) : EntitySystem() {

    private val assets by di.instance<AssetManager>()
    private val batch by di.instance<SpriteBatch>()
    private val camera by di.instance<OrthographicCamera>("gui")
    private val connectionManager by di.instance<ConnectionManager>()

    val font: BitmapFont by assets.loadFreeTypeFont(PATH_FONT_NOTO) {
        size = 14
        color = Color.BLACK
        incremental = true
    }

    override fun addedToEngine(engine: Engine?) {
        assets.finishLoadingAsset<BitmapFont>(PATH_FONT_NOTO)
    }

    override fun update(deltaTime: Float) {
        batch.use(camera.combined) {
            drawPing()
        }
    }

    private fun drawPing() {
        var logText = "fps: " + Gdx.graphics.framesPerSecond
        if (connectionManager.currentPing >= 0) {
            logText += " - ping: ${connectionManager.currentPing}"
        }
        font.draw(batch, logText, -camera.viewportWidth / 2f + 8, -camera.viewportHeight / 2f + 2 + font.lineHeight)
    }

}
