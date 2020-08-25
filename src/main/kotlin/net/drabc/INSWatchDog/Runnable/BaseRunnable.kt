package net.drabc.INSWatchDog.Runnable

import kotlinx.coroutines.delay
import net.drabc.INSWatchDog.Logger
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Vars.Var

open class BaseRunnable(_delayTime: Int = Var.settingBase.setting.waitTime, _forceExec: Boolean = false) {
    private var delayTime = 0
    private var forceExec = false
    open suspend fun execute(client: RconClient){
        //Dummy
    }
    suspend fun run(client: RconClient) {
        Var.logger.log("启动${this.javaClass.simpleName}协程",
            Logger.LogType.WARN
        )
        while (true) {
            if(LogWatcher.gameStatue >= LogWatcher.GameStatues.WaitingToStart || forceExec)
                execute(client)
            else
                Var.logger.log("游戏未开始，跳过执行 ${this.javaClass.name}")
            delay((delayTime * 1000).toLong())
        }
    }
    init {
        delayTime = _delayTime
        forceExec = _forceExec
    }
}