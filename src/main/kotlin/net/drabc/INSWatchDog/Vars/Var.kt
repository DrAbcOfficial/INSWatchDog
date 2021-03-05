package net.drabc.inswatchdog.vars
import net.drabc.inswatchdog.Logger
import net.drabc.inswatchdog.getOS
import net.drabc.inswatchdog.setting.SettingBase

object Var {
    lateinit var settingBase: SettingBase
    var osType = getOS()
    val defaultSettingBase: SettingBase by lazy { settingBase.copy() }
    var mapsName: MutableList<String> = mutableListOf()
    var playerList: MutableList<Player> = mutableListOf()
    val logger: Logger = Logger()
    var nowDifficult: Double = 0.0
    var nowBotCount : Int = 0
}