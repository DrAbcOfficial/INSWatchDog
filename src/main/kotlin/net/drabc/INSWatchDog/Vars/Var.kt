package net.drabc.INSWatchDog.Vars
import net.drabc.INSWatchDog.Logger
import net.drabc.INSWatchDog.Setting.SettingBase
import sun.awt.OSInfo

object Var {
    lateinit var settingBase: SettingBase
    var mapsName: MutableList<String> = mutableListOf()
    var playerList: MutableList<Player> = mutableListOf()
    val logger: Logger = Logger()
    var nowDifficult: Double = 0.0
    var nowBotCount : Int = 0
    val systemType: OSInfo.OSType = OSInfo.getOSType()
}