package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.doorbash.agario.helpers.ConnectionManager
import io.github.doorbash.agario.helpers.ConnectionState
import io.github.doorbash.agario.helpers.PATH_FONT_NOTO
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.freetype.async.loadFreeTypeFont
import ktx.graphics.use
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class GuiRenderSystem(
        val camera: OrthographicCamera,
        val assets: AssetStorage,
        val batch: SpriteBatch,
        val connectionManager: ConnectionManager
) : EntitySystem() {

    var font: BitmapFont? = null

    override fun addedToEngine(engine: Engine?) {
        KtxAsync.launch {
            font = assets.loadFreeTypeFont(PATH_FONT_NOTO) {
                size = 14
                color = com.badlogic.gdx.graphics.Color.BLACK
                incremental = true
            }
        }
    }

    override fun update(deltaTime: Float) {
        if(font == null) return

        batch.use(camera.combined) {
            if (connectionManager.connectionState == ConnectionState.CONNECTION_STATE_CONNECTED)
                drawPing()
            else
                drawConnecting()
        }
    }

    private fun drawPing() {
        var logText = "fps: " + Gdx.graphics.framesPerSecond
        if (connectionManager.currentPing >= 0) {
            logText += " - ping: ${connectionManager.currentPing}"
        }
        font!!.draw(batch, logText, -camera.viewportWidth / 2f + 8, -camera.viewportHeight / 2f + 2 + font!!.lineHeight)
    }

    private fun drawConnecting() {
        font!!.draw(batch, "Connecting...", -camera.viewportWidth / 2f + 8, -camera.viewportHeight / 2f + 2 + font!!.lineHeight)
    }
}
