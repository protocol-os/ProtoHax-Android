package club.fdpclient.club.script

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import dev.sora.protohax.MyApplication
import dev.sora.protohax.util.ContextUtils.toast
import dev.sora.relay.cheat.value.BoolValue
import dev.sora.relay.cheat.value.FloatValue
import dev.sora.relay.cheat.value.IntValue
import dev.sora.relay.utils.logInfo


class ScriptApi(val script: Script) {
	val thePlayer
		get() = run { script.session.player }

	fun escapeHtml(input: String): String {
		val escaped = StringBuilder()
		for (c in input.toCharArray()) {
			when (c) {
				'<' -> escaped.append("&lt;")
				'>' -> escaped.append("&gt;")
				'&' -> escaped.append("&amp;")
				'"' -> escaped.append("&quot;")
				'\'' -> escaped.append("&#39;")
				else -> escaped.append(c)
			}
		}
		return escaped.toString()
	}

	fun toast(str: String) {
		val handler = Handler(Looper.getMainLooper())
		handler.post {
			logInfo("Message: $str")
			Toast.makeText(
				MyApplication.instance,
				str,
				Toast.LENGTH_SHORT
			).show()
		}

	}

	fun tosat(str: String) {
		MyApplication.instance.toast(str)
	}

	fun registerFloatValue(
		name: String,
		value: Float,
		minValue: Float,
		maxValue: Float,
		chinese: String = name
	): FloatValue {
		val value = FloatValue(name, value, minValue..maxValue)
		script.values.add(value)
		return value
	}

	fun registerIntValue(
		name: String,
		value: Int,
		minValue: Int,
		maxValue: Int,
		chinese: String = name
	): IntValue {
		val value = IntValue(name, value, minValue..maxValue)
		script.values.add(value)
		return value
	}

	fun registerBoolValue(name: String, value: Boolean, chinese: String = name): BoolValue {
		val value = BoolValue(name, value)
		script.values.add(value)
		return value
	}
}
