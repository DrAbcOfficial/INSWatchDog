package net.drabc.INSWatchDog

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Setting.SettingBase
import net.drabc.INSWatchDog.Vars.Player
import net.drabc.INSWatchDog.Vars.Var
import java.nio.file.Files
import java.nio.file.Paths


object Utility {
    fun parseJson(name: String) : Any? {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<SettingBase> = moshi.adapter<SettingBase>(SettingBase::class.java)
        Var.logger.Log("尝试读取${Paths.get(getUserDir() + name)}", Logger.LogType.WARN)
        val stream = Files.newInputStream(Paths.get(getUserDir() + name))
        stream.buffered().reader().use { reader -> return jsonAdapter.fromJson(reader.readText()) }
    }

    fun sendCommand(client: RconClient, command: String, hide : Boolean = false): String{
        val tempString = client.sendCommand(command)
        if(!hide){
            Var.logger.Log("已执行命令: ${tempString}")
        }
        return tempString
    }

    fun kickPlayer(client: RconClient, player: Player, reason: String) {
        sendCommand(client,"kick ${player.NetID} $reason")
        Var.logger.Log("已踢出玩家: ${player.Name}[${player.NetID}]\t|\t${reason}\t|\t${player.IP}", Logger.LogType.WARN)
    }

    fun sendMessage(client: RconClient, message: String){
        sendCommand(client,"say [${Var.settingBase.message.msgHeader}] $message", true)
        Var.logger.Log("已发送消息: ${message}")
    }

    fun getUserDir() : String{
        return System.getProperty("user.dir")
    }

    fun getMapList(client: RconClient){
        sendCommand(client, "maps", true).split('\n').forEach{
            if(!it.isNullOrBlank())
                Var.mapsName.add(it.trim())
        }
    }
}