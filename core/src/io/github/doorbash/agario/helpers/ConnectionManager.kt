package io.github.doorbash.agario.helpers

import com.badlogic.ashley.core.Engine
import io.colyseus.Client
import io.colyseus.Room
import io.github.doorbash.agario.classes.GameState
import io.github.doorbash.agario.ecs.createFruit
import io.github.doorbash.agario.ecs.createPlayer
import io.github.doorbash.agario.helpers.ConnectionState.*
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.logger

private val LOG = logger<ConnectionManager>()

enum class ConnectionState {
    CONNECTION_STATE_DISCONNECTED,
    CONNECTION_STATE_CONNECTING,
    CONNECTION_STATE_CONNECTED
}

class ConnectionManager(
        val engine: Engine
) {
    val client = Client(ENDPOINT)
    var connectionState: ConnectionState = CONNECTION_STATE_DISCONNECTED
    var sessionId: String? = null
    var room: Room<GameState>? = null
    var lastPingSentTime = 0L
    var lastPingReplyTime = 0L
    var currentPing = -1L
    var lerp = LERP_MAX

    fun connectIfNeeded() {
        if (connectionState != CONNECTION_STATE_DISCONNECTED) return
        connectionState = CONNECTION_STATE_CONNECTING

        KtxAsync.launch {
            try {
                if (sessionId == null) room = client.joinOrCreate(GameState::class.java, ROOM_NAME)
                else room = client.reconnect(GameState::class.java, ROOM_NAME, sessionId!!)
                connectionState = CONNECTION_STATE_CONNECTED
                updateRoom(room!!)
            } catch (e: Exception) {
                connectionState = CONNECTION_STATE_DISCONNECTED
                room = null
                LOG.error(e) { "error while connecting to room $ROOM_NAME" }
            }
        }
    }

    private fun updateRoom(room: Room<GameState>) {
        this.room = room
        sessionId = room.sessionId
        LOG.debug { "joined ${room.name}" }
        engine.removeAllEntities()
        lastPingReplyTime = 0
        room.onLeave = { code ->
            LOG.debug { "left public, code = $code" }
            if (code > 1000) {
                // abnormal disconnection!
                connectionState = CONNECTION_STATE_DISCONNECTED
                this.room = null
            }
        }
        room.onError = { code, message ->
            connectionState = CONNECTION_STATE_DISCONNECTED
            this.room = null
            LOG.error { "onError($code, $message)" }
        }
        room.onMessage("ping") { message: String ->
            lastPingReplyTime = System.currentTimeMillis()
            currentPing = lastPingReplyTime - lastPingSentTime
            calculateLerp(currentPing.toFloat())
        }
        room.state.players.onAdd = label@{ player, key ->
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            KtxAsync.launch {
                engine.createPlayer(player, key)
            }
        }
        room.state.players.onRemove = label@{ player, key ->
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            KtxAsync.launch {
                try {
                    engine.removeEntity(player.entity)
                } catch (e: Exception) {
                }
            }
        }
        room.state.fruits.onAdd = label@{ fruit, key ->
            LOG.debug { "fruit added: $key" }
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            KtxAsync.launch {
                engine.createFruit(fruit)
            }
        }
        room.state.fruits.onRemove = label@{ fruit, key ->
            LOG.debug { "fruit removed: $key" }
            if (fruit.key != key) LOG.error { ("WTF ${fruit.key} != $key") }
            if (connectionState != CONNECTION_STATE_CONNECTED) return@label
            KtxAsync.launch {
                try {
//                    LOG.debug { "thread: " + Thread.currentThread().name }
                    engine.removeEntity(fruit.entity)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun calculateLerp(currentLatency: Float) {
        val latency: Float = if (currentLatency < LATENCY_MIN) LATENCY_MIN.toFloat()
        else if (currentLatency > LATENCY_MAX) LATENCY_MAX.toFloat()
        else currentLatency
        lerp = LERP_MAX + (latency - LATENCY_MIN) / (LATENCY_MAX - LATENCY_MIN) * (LERP_MIN - LERP_MAX)
        LOG.debug { "current latency: $currentLatency ms" }
        LOG.debug { "lerp : $lerp" }
    }

    fun sendPing() {
        if (room == null) return
        room?.send("ping", "pong")
        lastPingSentTime = System.currentTimeMillis()
    }

    fun sendAngle(angle: Int) {
        if (room == null) return
        room?.send("angle", angle)
    }

    fun dispose() {
        room?.leave()
    }
}