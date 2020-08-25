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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Utility {
    fun parseJson(name: String) : Any? {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<SettingBase> = moshi.adapter<SettingBase>(SettingBase::class.java)
        Var.logger.log("尝试读取${Paths.get(getUserDir() + name)}", Logger.LogType.WARN)
        val stream = Files.newInputStream(Paths.get(getUserDir() + name))
        stream.buffered().reader().use { reader -> return jsonAdapter.fromJson(reader.readText()) }
    }

    private fun keyWordReplace(word: String): String{
        require(!word.isBlank()){Var.logger.log("输入值为空白", Logger.LogType.SEVERE)}
        var tempString = word
        if(tempString.contains("{time}")) {
            tempString = tempString.replace(
                "{time}",
                DateTimeFormatter.ofPattern(Var.settingBase.message.format.time).format(LocalDateTime.now())
            )
        }
        if(tempString.contains("{bestplayer}")){
            val tempPlayer = Var.playerList.maxBy { p -> p.Score }
            if (tempPlayer != null) {
                tempString = tempString.replace(
                    "{bestplayer}",
                    if(tempPlayer.ID != 0 && tempPlayer.Score != 0.toLong()){
                        tempPlayer.Name +
                                Var.settingBase.message.format.lastBest.replace(
                                    "{0}",
                                    tempPlayer.Score.toString()
                                )
                    }
                    else
                        Var.settingBase.message.format.noBest)
            }
        }
        if(tempString.contains("{player}")){
            tempString = tempString.replace("{player}", Var.playerList.size.toString())
        }
        if(tempString.contains("{maps}")){
            tempString = tempString.replace("{maps}", Var.mapsName.size.toString())
        }
        if(tempString.contains("{difficult}")){
            tempString = tempString.replace("{difficult}", Var.nowDifficult.toString())
        }
        return tempString
    }

    fun sendCommand(client: RconClient, command: String, hide : Boolean = false): String{
        val tempString = client.sendCommand(command)
        if(!hide){
            Var.logger.log("已执行命令: $tempString")
        }
        return tempString
    }

    fun kickPlayer(client: RconClient, player: Player, reason: String) {
        sendCommand(client,"kick ${player.NetID} $reason")
        Var.logger.log("已踢出玩家: ${player.Name}[${player.NetID}]\t|\t${reason}\t|\t${player.IP}", Logger.LogType.WARN)
    }

    fun sendMessage(client: RconClient, message: String, header: Boolean = true): Boolean{
        return try {
            val replacedMessage =
                "${if(header) 
                    "[${ net.drabc.INSWatchDog.Vars.Var.settingBase.message.msgHeader}]"
                else
                    ""
                } ${keyWordReplace(message)}"
            sendCommand(client, "say $replacedMessage", true)
            Var.logger.log("已发送消息: $replacedMessage")
            true
        } catch (e:Exception){
            false
        }
    }

    fun getUserDir() : String{
        return System.getProperty("user.dir")
    }

    fun getMapList(client: RconClient){
        sendCommand(client, "maps", true).split('\n').forEach{
            if(!it.isBlank())
                Var.mapsName.add(it.trim())
        }
    }
}