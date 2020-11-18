package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CVars(
    var defaultSets: List<String> = listOf("bEnforceFriendlyFireReflect True", "MinimumTotalFriendlyFireDamageToReflect 300")
)