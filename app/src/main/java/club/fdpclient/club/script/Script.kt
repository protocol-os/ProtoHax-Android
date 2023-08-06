package club.fdpclient.club.script

import android.annotation.SuppressLint
import dev.sora.protohax.relay.MinecraftRelay
import dev.sora.relay.cheat.module.CheatCategory
import dev.sora.relay.cheat.module.CheatModule
import dev.sora.relay.game.event.EventDisconnect
import dev.sora.relay.game.event.EventEntitySpawn
import dev.sora.relay.game.event.EventPacketInbound
import dev.sora.relay.game.event.EventPacketOutbound
import dev.sora.relay.game.event.EventPacketPostOutbound
import dev.sora.relay.game.event.EventPostTick
import dev.sora.relay.game.event.EventTick
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File


@SuppressLint("SuspiciousIndentation")
class Script(
    private val file: File, name: String,
    defaultOn: Boolean = false,
    canToggle: Boolean = true
) : CheatModule(name,CheatCategory.MISC,defaultOn,canToggle) {
    private val moduleScript: Globals = JsePlatform.standardGlobals()
    private var onDisable: LuaValue
    private var onEnable: LuaValue
	private var onTick: LuaValue
	private var onPostTick: LuaValue
	private var onDisconnect: LuaValue
	private var onEntitySpawn: LuaValue
    private var onPacketInbound: LuaValue
	private var onPacketOutbound: LuaValue
	private var onPostPacketOutbound: LuaValue

    init {
        moduleScript.loadfile(file.absolutePath).call()
        onDisable = moduleScript.get(LuaValue.valueOf("onDisable"))
        onEnable = moduleScript[LuaValue.valueOf("onEnable")]
		onTick = moduleScript.get(LuaValue.valueOf("onTick"))
		onPostTick = moduleScript.get(LuaValue.valueOf("onPostTick"))
		onDisconnect = moduleScript.get(LuaValue.valueOf("onDisconnect"))
		onEntitySpawn = moduleScript.get(LuaValue.valueOf("onEntitySpawn"))
        onPacketInbound = moduleScript.get(LuaValue.valueOf("onPacketInbound"))
		onPacketOutbound = moduleScript.get(LuaValue.valueOf("onPacketOutbound"))
		onPostPacketOutbound = moduleScript.get(LuaValue.valueOf("onPostPacketOutbound"))
		try {
			moduleScript.get(LuaValue.valueOf("onLoad")).call(CoerceJavaToLua.coerce(ScriptApi(this)))
		} catch (e: Exception) {
			e.printStackTrace()
		}
    }

    private fun run(method: LuaValue) {
        try {
            method.call()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun run(method: LuaValue, value: LuaValue) {
        try {
            method.call(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        super.onDisable()
        run(onDisable)
    }

	override fun onEnable() {
		super.onEnable()
		run(onEnable,CoerceJavaToLua.coerce(MinecraftRelay.session))
	}

	private val EventOnTick = handle<EventTick> {
		run(onTick, CoerceJavaToLua.coerce(this))
	}
	private val EventOnPostTick = handle<EventPostTick> {
		run(onPostTick, CoerceJavaToLua.coerce(this))
	}
	private val EventEntitySpawn = handle<EventEntitySpawn> {
		run(onEntitySpawn, CoerceJavaToLua.coerce(this))
	}

	private val EventDisconnect = handle<EventDisconnect> {
		run(onDisconnect, CoerceJavaToLua.coerce(this))
	}

	private val EventPacketOutbound = handle<EventPacketOutbound> {
		run(onPacketOutbound, CoerceJavaToLua.coerce(this))
	}

	private val EventonTick = handle<EventTick> {
		run(onTick, CoerceJavaToLua.coerce(this))
	}

	private val EventPacketInbound = handle<EventPacketInbound> {
		run(onPacketInbound, CoerceJavaToLua.coerce(this))
	}

	private val EventPostPacketOutbound = handle<EventPacketPostOutbound> {
		run(onPostPacketOutbound, CoerceJavaToLua.coerce(this))
	}
}
