package io.github.doorbash.agario

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import io.colyseus.Client
import io.colyseus.Room
import io.github.doorbash.agario.classes.Fruit
import io.github.doorbash.agario.classes.GameState
import io.github.doorbash.agario.classes.Player
import kotlinx.coroutines.*
import ktx.app.KtxScreen
import java.lang.Exception
import java.util.*

class GameScreen(val game: Game) : KtxScreen {
    /* **************************************** FIELDS *******************************************/
    private var connectionState: Int = CONNECTION_STATE_DISCONNECTED
    private var lastAngle = -1000
    private val mapWidth = 1200
    private val mapHeight = 1200
    private var lastSendDirectionTime: Long = 0
    private var lastPingSentTime: Long = 0
    private var lastPingReplyTime: Long = 0
    private var currentPing: Long = -1
    private var lastConnectionCheckTime: Long = 0
    private var lerp: Float = LERP_MAX
    private var sessionId: String? = null
    private var roomId: String? = null
    private var batch: SpriteBatch? = null
    private var shapeRenderer: ShapeRenderer? = null
    private var camera: OrthographicCamera? = null
    private var guiCamera: OrthographicCamera? = null
    private var freetypeGeneratorNoto: FreeTypeFontGenerator? = null
    private var logFont: BitmapFont? = null
    private var room: Room<GameState>? = null
    private val fruits: HashMap<String, Fruit> = HashMap()
    private val players: HashMap<String, Player> = HashMap()
    private var connectToServerJob: Job? = null

    /* *************************************** OVERRIDE *****************************************/
    init {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera!!.update()
        guiCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        guiCamera!!.update()
        freetypeGeneratorNoto = FreeTypeFontGenerator(Gdx.files.internal(PATH_FONT_NOTO))
        val logFontParams = FreeTypeFontParameter()
        logFontParams.size = 14
        logFontParams.color = Color.BLACK
        logFontParams.flip = false
        logFontParams.incremental = true
        logFont = freetypeGeneratorNoto!!.generateFont(logFontParams)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.98f, 0.99f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        updatePositions()
        adjustCamera()
        if (connectionState == CONNECTION_STATE_CONNECTED) {
            shapeRenderer!!.projectionMatrix = camera!!.combined
            shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)
//            drawGrid()
            drawFruits()
            drawPlayers()
            shapeRenderer!!.end()
            batch!!.projectionMatrix = guiCamera!!.combined
            batch!!.begin()
            drawPing()
            batch!!.end()
        }
        val now = System.currentTimeMillis()
        if (now - lastSendDirectionTime >= SEND_DIRECTION_INTERVAL) {
            lastSendDirectionTime = now
            sendDirection()
        }
        if (now - lastPingSentTime >= PING_INTERVAL) {
            lastPingSentTime = now
            sendPing()
        }
        if (now - lastConnectionCheckTime >= CHECK_CONNECTION_INTERVAL) {
            lastConnectionCheckTime = now
            checkConnection()
        }
    }

    override fun dispose() {
        if (logFont != null) logFont!!.dispose()
        if (batch != null) batch!!.dispose()
        if (shapeRenderer != null) shapeRenderer!!.dispose()
        if (freetypeGeneratorNoto != null) freetypeGeneratorNoto!!.dispose()
        if (room != null) room!!.leave()
        connectToServerJob?.cancel()
    }

    override fun resize(width: Int, height: Int) {
        camera!!.viewportWidth = width.toFloat()
        camera!!.viewportHeight = height.toFloat()
        camera!!.update()
        guiCamera!!.viewportWidth = width.toFloat()
        guiCamera!!.viewportHeight = height.toFloat()
        guiCamera!!.update()
    }

    /* ***************************************** DRAW *******************************************/
    private fun drawGrid() {
        var player: Player? = null
        var leftBottomX: Int
        var leftBottomY: Int
        val rightTopX: Int
        val rightTopY: Int
        val width = camera!!.zoom * camera!!.viewportWidth
        val height = camera!!.zoom * camera!!.viewportHeight
        if (room != null && room!!.state.players.get(room!!.sessionId).also { player = it } != null) {
            leftBottomX = (player!!.position.x - width / 2f).toInt()
            leftBottomY = (player!!.position.y - height / 2f).toInt()
        } else {
            leftBottomX = (-width / 2f).toInt()
            leftBottomY = (-height / 2f).toInt()
        }
        leftBottomX -= leftBottomX % GRID_SIZE + GRID_SIZE
        leftBottomY -= leftBottomY % GRID_SIZE + GRID_SIZE
        rightTopX = (leftBottomX + width + 2 * GRID_SIZE).toInt()
        rightTopY = (leftBottomY + height + 2 * GRID_SIZE).toInt()
        shapeRenderer!!.color = Color.BLACK
        run {
            var i = leftBottomX
            while (i < rightTopX) {
                shapeRenderer!!.line(i.toFloat(), leftBottomY.toFloat(), i.toFloat(), rightTopY.toFloat())
                i += GRID_SIZE
            }
        }
        var i = leftBottomY
        while (i < rightTopY) {
            shapeRenderer!!.line(leftBottomX.toFloat(), i.toFloat(), rightTopX.toFloat(), i.toFloat())
            i += GRID_SIZE
        }
        shapeRenderer!!.end()
        shapeRenderer!!.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer!!.color = Color.RED
        shapeRenderer!!.rect(0f, 0f, mapWidth.toFloat(), mapHeight.toFloat())
        shapeRenderer!!.end()
        shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun drawFruits() {
        if (room == null) return
        val thisPlayer: Player = room!!.state.players.get(room!!.sessionId) ?: return
        synchronized(fruits) {
            for (fr in fruits.values) {
                if (fr._color == null) continue
                if (!objectIsInViewport(thisPlayer, fr.position.x, fr.position.y, FRUIT_RADIUS.toFloat())) continue
                shapeRenderer!!.color = fr._color
                shapeRenderer!!.circle(fr.position.x, fr.position.y, FRUIT_RADIUS.toFloat())
            }
        }
    }

    private fun drawPlayers() {
        if (room == null) return
        val thisPlayer: Player = room!!.state.players.get(room!!.sessionId) ?: return
        synchronized(players) {
            for ((clientId, value) in players) {
                val player: Player = value ?: continue
                if (clientId == room!!.sessionId || objectIsInViewport(thisPlayer, player.position.x, player.position.y, player.radius)) {
                    shapeRenderer!!.color = player._strokeColor
                    shapeRenderer!!.circle(player.position.x, player.position.y, player.radius)
                    shapeRenderer!!.color = player._color
                    shapeRenderer!!.circle(player.position.x, player.position.y, player.radius - 3)
                }
            }
        }
    }

    private fun drawPing() {
        var logText = "fps: " + Gdx.graphics.framesPerSecond
        if (currentPing >= 0) {
            logText += " - ping: $currentPing"
        }
        logFont!!.draw(batch, logText, -guiCamera!!.viewportWidth / 2f + 8, -guiCamera!!.viewportHeight / 2f + 2 + logFont!!.lineHeight)
    }

    /* ***************************************** LOGIC *******************************************/
    private fun updatePositions() {
        if (room == null) return
        synchronized(players) {
            for ((clientId, value) in players) {
                val player: Player = value ?: continue
                if (clientId == room!!.sessionId) player.position.set(MathUtils.lerp(player.position.x, player.x, lerp), MathUtils.lerp(player.position.y, player.y, lerp)) else player.position.set(MathUtils.lerp(player.position.x, player.x, OTHER_PLAYERS_LERP), MathUtils.lerp(player.position.y, player.y, OTHER_PLAYERS_LERP))
            }
        }
    }

    private fun objectIsInViewport(player: Player?, x: Float, y: Float, radius: Float): Boolean {
        if (x + radius < player?.position?.x!! - camera!!.zoom * camera!!.viewportWidth / 2) return false
        if (x - radius > player.position?.x!! + camera!!.zoom * camera!!.viewportWidth / 2) return false
        if (y + radius < player.position?.y!! - camera!!.zoom * camera!!.viewportHeight / 2) return false
        return if (y - radius > player.position.y + camera!!.zoom * camera!!.viewportHeight / 2) false else true
    }

    private fun adjustCamera() {
        if (room == null) return
        val player: Player = room!!.state.players.get(room!!.sessionId) ?: return
        camera!!.position.x = player.position.x
        camera!!.position.y = player.position.y
        camera!!.zoom = player.radius / 60f
        camera!!.update()
    }

    /* **************************************** NETWORK ******************************************/
    private fun checkConnection() {
        if (connectionState == CONNECTION_STATE_CONNECTED && lastPingReplyTime > 0 && System.currentTimeMillis() - lastPingReplyTime > 15000) {
            connectionState = CONNECTION_STATE_DISCONNECTED
        }
        if (connectionState == CONNECTION_STATE_DISCONNECTED) {
            connectToServerJob = connectToServer()
        }
    }

    private fun connectToServer(): Job {
        return GlobalScope.launch {
            tailrec suspend fun connect() {
                println("connectToServer()")
                if (connectionState != CONNECTION_STATE_DISCONNECTED)
                    connectionState = CONNECTION_STATE_CONNECTING
                val client = Client(ENDPOINT)
                if (sessionId == null) {
                    updateRoom(client.joinOrCreate("ffa", GameState::class.java))
                } else {
                    updateRoom(client.reconnect(roomId!!, GameState::class.java, sessionId!!))
                }
                if (room == null) {
                    delay(3000)
                    connect()
                }
            }
            connect()
        }
    }

    private fun updateRoom(room: Room<GameState>) {
        this.room = room
        roomId = room.id
        sessionId = room.sessionId
        println("joined chat")
        synchronized(players) { players.clear() }
        synchronized(fruits) { fruits.clear() }
        connectionState = CONNECTION_STATE_CONNECTED
        lastPingReplyTime = 0
        room.onLeave = { code ->
            println("left public, code = $code")
            if (code > 1000) {
                // abnormal disconnection!
                connectionState = CONNECTION_STATE_DISCONNECTED
            }
        }
        room.onError = { code, message ->
            connectionState = CONNECTION_STATE_DISCONNECTED
            println("onError()")
            println(message)
            room.serializer.schemaPrint()
        }
        room.onMessage("ping") { message: String ->
            lastPingReplyTime = System.currentTimeMillis()
            currentPing = lastPingReplyTime - lastPingSentTime
            calculateLerp(currentPing.toFloat())
        }
        room.state.players.onAdd = onAdd@{ player: Player, key: String ->
            if (connectionState != CONNECTION_STATE_CONNECTED) return@onAdd
            synchronized(players) { players.put(key, player) }
            //            System.out.println("new player added >> clientId: " + key);
            player.position.x = player.x
            player.position.y = player.y
            player._color = Color(player.color)
            player._strokeColor = Color(player.color)
            player._strokeColor?.mul(0.9f)
        }
        room.state.players.onRemove = label@{ player, key ->
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            synchronized(players) { players.remove(key) }
        }
        room.state.fruits.onAdd = label@{ fruit, key ->
            if(fruit.key != key) {
                throw Exception("WTF " + fruit.key + " != " + key)
            }
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            synchronized(fruits) { fruits.put(key, fruit) }
            //            System.out.println("new fruit added >> key: " + key);
            fruit.position.x = fruit.x
            fruit.position.y = fruit.y
            fruit._color = Color(fruit.color)
        }
        room.state.fruits.onRemove = label@{ fruit, key ->
            if(fruit.key != key) {
                throw Exception("WTF " + fruit.key + " != " + key)
            }
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            synchronized(fruits) { fruits.remove(key) }
        }
    }

    private fun sendDirection() {
        if (room == null) return
        val dx = Gdx.input.x - camera!!.viewportWidth / 2
        val dy = Gdx.input.y - camera!!.viewportHeight / 2
        val angle = Math.toDegrees(Math.atan2(-dy.toDouble(), dx.toDouble())).toInt()
        if (lastAngle != angle) {
            room?.send("angle", angle)
            lastAngle = angle
        }
    }

    private fun sendPing() {
        if (room == null) return
        val data = LinkedHashMap<String, Any>()
        data["op"] = "ping"
        room?.send("ping", "pong")
    }

    private fun calculateLerp(currentLatency: Float) {
        val latency: Float
        latency = if (currentLatency < LATENCY_MIN) LATENCY_MIN.toFloat() else if (currentLatency > LATENCY_MAX) LATENCY_MAX.toFloat() else currentLatency
        lerp = LERP_MAX + (latency - LATENCY_MIN) / (LATENCY_MAX - LATENCY_MIN) * (LERP_MIN - LERP_MAX)
        println("current latency: $currentLatency ms")
        println("lerp : $lerp")
    }

    companion object {
        /* *************************************** CONSTANTS *****************************************/
        private const val ENDPOINT = "ws://127.0.0.1:2560"
        private const val PATH_FONT_NOTO = "fonts/NotoSans-Regular.ttf"
        private const val SEND_DIRECTION_INTERVAL = 200
        private const val PING_INTERVAL = 5000
        private const val CHECK_CONNECTION_INTERVAL = 3000
        private const val GRID_SIZE = 60
        private const val FRUIT_RADIUS = 10
        private const val LATENCY_MIN = 100 // ms
        private const val LATENCY_MAX = 500 // ms
        private const val CONNECTION_STATE_DISCONNECTED = 0
        private const val CONNECTION_STATE_CONNECTING = 1
        private const val CONNECTION_STATE_CONNECTED = 2
        private const val LERP_MIN = 0.1f
        private const val LERP_MAX = 0.5f
        private const val OTHER_PLAYERS_LERP = 0.5f
    }
}