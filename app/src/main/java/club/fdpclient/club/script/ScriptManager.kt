package club.fdpclient.club.script

import club.fdpclient.club.script.Script
import dev.sora.protohax.relay.MinecraftRelay
import dev.sora.relay.game.GameSession

class ScriptManager {
    private val scripts = mutableListOf<Script>()
    lateinit var session : GameSession

    fun registerModule(script: Script) {
        MinecraftRelay.moduleManager.registerModule(script)
    }
}
