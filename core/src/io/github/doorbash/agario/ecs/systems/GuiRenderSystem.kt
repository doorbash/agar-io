package io.github.doorbash.agario.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import io.github.doorbash.agario.helpers.ConnectionManager.currentPing
import io.github.doorbash.agario.helpers.PATH_FONT_NOTO
import ktx.graphics.use
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class GuiRenderSystem(
        private val batch: SpriteBatch,
        private val camera: OrthographicCamera
) : EntitySystem() {

    private var freetypeGeneratorNoto: FreeTypeFontGenerator? = null
    private var logFont: BitmapFont? = null

    override fun addedToEngine(engine: Engine?) {
        // TODO: use asset manager
        freetypeGeneratorNoto = FreeTypeFontGenerator(Gdx.files.internal(PATH_FONT_NOTO))
        val logFontParams = FreeTypeFontGenerator.FreeTypeFontParameter()
        logFontParams.size = 14
        logFontParams.color = Color.BLACK
        logFontParams.flip = false
        logFontParams.incremental = true
        logFont = freetypeGeneratorNoto!!.generateFont(logFontParams)
    }

    override fun removedFromEngine(engine: Engine?) {
        freetypeGeneratorNoto?.dispose()
        logFont?.dispose()
    }

    override fun update(deltaTime: Float) {
        batch.use(camera.combined) {
            drawPing()
        }
    }

    private fun drawPing() {
        var logText = "fps: " + Gdx.graphics.framesPerSecond
        if (currentPing >= 0) {
            logText += " - ping: $currentPing"
        }
        logFont!!.draw(batch, logText, -camera.viewportWidth / 2f + 8, -camera.viewportHeight / 2f + 2 + logFont!!.lineHeight)
    }

}
