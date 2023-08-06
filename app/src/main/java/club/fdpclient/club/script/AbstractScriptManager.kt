package club.fdpclient.club.script

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.sora.protohax.MyApplication
import dev.sora.protohax.relay.MinecraftRelay
import dev.sora.protohax.util.ContextUtils.toast
import dev.sora.relay.utils.logError
import java.io.File
import java.io.InputStream

abstract class AbstractScriptManager(val scriptManager: ScriptManager) {

    abstract fun listScript(): List<String>

    protected abstract fun loadScriptData(name: String): InputStream?
    protected abstract fun getScriptFile(name: String): File

    protected abstract fun saveScriptData(name: String, data: ByteArray)

    abstract fun deleteScript(name: String): Boolean

    open fun copyScript(src: String, dst: String): Boolean {
        val reader = loadScriptData(src) ?: return false
        saveScriptData(dst, reader.readBytes())
        return true
    }

    open fun renameScript(src: String, dst: String): Boolean {
        if (!copyScript(src, dst)) return false
        return deleteScript(dst)
    }

    /**
     * @return false if failed to load the Script or Script not exists
     */
    open fun loadScript(name: String): Boolean {
        return try {
            scriptManager.registerModule(Script(getScriptFile(name),name))

            true
        } catch (t: Throwable) {

			Handler(Looper.getMainLooper()).post(Runnable {
				Toast.makeText(
					MyApplication.instance,
					"[Script]failed to load $name",
					Toast.LENGTH_LONG
				).show()
			})
            logError("failed to load Script", t)
            false
        }
    }

    open fun saveScript(name: String) {
        try {
            val json = JsonObject()
            saveScriptData(name, DEFAULT_GSON.toJson(json).toByteArray(Charsets.UTF_8))
        } catch (t: Throwable) {
            logError("failed to save Script", t)
        }
    }

    companion object {
        val DEFAULT_GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    }
}
