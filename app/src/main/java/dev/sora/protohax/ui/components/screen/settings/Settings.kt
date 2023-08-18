package dev.sora.protohax.ui.components.screen.settings

import android.os.Build
import dev.sora.protohax.R
import dev.sora.protohax.relay.MinecraftRelay

object Settings {

	val offlineSessionEncryption = BoolSetting(R.string.setting_encryption, R.string.setting_encryption_desc, "OFFLINE_SESSION_ENCRYPTION", false)
	val enableCommandManager = BoolSetting(R.string.setting_commands, R.string.setting_commands_desc, "ENABLE_COMMAND_MANAGER", true, restartRequired = true)
	val enableRakReliability = BoolSetting(R.string.setting_rak_reliability, R.string.setting_rak_reliability_desc, "ENABLE_RAK_RELIABILITY", true) {
		MinecraftRelay.updateReliability()
	}
	val trustClicks = BoolSetting(R.string.setting_trust_click,
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) R.string.setting_trust_click_desc else R.string.setting_trust_click_disabled,
		"TRUST_CLICK", false, override = Build.VERSION.SDK_INT < Build.VERSION_CODES.S, true)
	val ipv6Status = TabSetting(R.string.setting_ip, "INTERNET_PROTOCOL", IPv6Choices.AUTOMATIC, IPv6Choices.values())
	val languages = TabSetting(R.string.setting_languages, "en_us", LanguagesChoices.ENGLISH, LanguagesChoices.values())

	val settings = arrayOf(offlineSessionEncryption, enableCommandManager, enableRakReliability, trustClicks, ipv6Status, languages)
	enum class LanguagesChoices(override val displayName: Int, override val internalName: String) : TabChoice {
		ENGLISH(R.string.setting_enus, "en_us"),
		CHINESE(R.string.setting_zhcn, "zh_cn"),
		JAPANESE(R.string.setting_jajp, "ja_jp"),
		RUSSIAN(R.string.setting_ruru, "ru_ru"),
	}
	enum class IPv6Choices(override val displayName: Int, override val internalName: String) : TabChoice {
		AUTOMATIC(R.string.setting_ip_auto, "auto"),
		ENABLED(R.string.setting_ip_enabled, "enabled"),
		DISABLED(R.string.setting_ip_disabled, "disabled"),
		V6ONLY(R.string.setting_ip_only, "v6only"),
	}
}
