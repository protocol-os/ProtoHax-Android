package club.fdpclient.club.script

import dev.sora.relay.utils.logError
import java.io.File
import java.io.InputStream

class ScriptManagerFileSystem(private val dir: File, private val suffix: String, scriptManager: ScriptManager) : AbstractScriptManager(scriptManager) {
    init {
        if (!dir.exists())
            dir.mkdirs()
    }
    public override fun getScriptFile(name: String): File {
        return File(dir, "$name.lua")
    }
    override fun listScript(): List<String> {
        return (dir.listFiles() ?: return emptyList())
            .filter { it.name.endsWith(suffix) }
            .map { it.name.let { it.substring(0, it.length - suffix.length) } }
    }
    override fun loadScriptData(name: String): InputStream? {
        val scriptFile = getScriptFile(name)
        if (!scriptFile.exists()) {
            return null
        }
        return scriptFile.inputStream()
    }
    override fun saveScriptData(name: String, data: ByteArray) {
        val scriptFile = getScriptFile(name)
        scriptFile.writeBytes(data)
    }
    override fun deleteScript(name: String): Boolean {
        val scriptFile = getScriptFile(name)
        if (scriptFile.exists())
            return scriptFile.delete()
        return false
    }
    override fun copyScript(src: String, dst: String): Boolean {
        val srcFile = getScriptFile(src)
        if (!srcFile.exists()) return false
        val dstFile = getScriptFile(dst)
        if (dstFile.exists()) return false
        return try {
            srcFile.copyTo(dstFile)
            true
        } catch (t: Throwable) {
            logError("error whilst copy Script", t)
            false
        }
    }

    override fun renameScript(src: String, dst: String): Boolean {
        val srcFile = getScriptFile(src)
        if (!srcFile.exists()) return false
        val dstFile = getScriptFile(dst)
        if (dstFile.exists()) return false
        srcFile.renameTo(dstFile)
        return true
    }
}
