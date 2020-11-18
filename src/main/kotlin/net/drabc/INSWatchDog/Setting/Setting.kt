package net.drabc.inswatchdog.setting

import com.squareup.moshi.JsonClass
import net.drabc.inswatchdog.Utility

@JsonClass(generateAdapter = true)
data class Setting(
    var defaultCvarSets: String = "default",
    var rtvMapList: List<String> = listOf("Scenario_Crossing_Checkpoint_Security", "Scenario_Farmhouse_Checkpoint_Security"),
    var hideEmpty: Boolean = false,
    var rootDir: String = Utility.getUserDir(),
    var serverName: String = "INS Server Difficulty[{0}]",
    var waitTime: Int = 5,
    var forceExec : Boolean = false
)