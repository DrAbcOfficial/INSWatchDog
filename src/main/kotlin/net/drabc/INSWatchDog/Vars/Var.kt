package net.drabc.inswatchdog.vars
import net.drabc.inswatchdog.Logger
import net.drabc.inswatchdog.setting.SettingBase
import sun.awt.OSInfo

object Var {
    lateinit var settingBase: SettingBase
    val defaultSettingBase: SettingBase by lazy { settingBase.copy() }
    var mapsName: MutableList<String> = mutableListOf()
    var playerList: MutableList<Player> = mutableListOf()
    val logger: Logger = Logger()
    var nowDifficult: Double = 0.0
    var nowBotCount : Int = 0
    val systemType: OSInfo.OSType = OSInfo.getOSType()
}