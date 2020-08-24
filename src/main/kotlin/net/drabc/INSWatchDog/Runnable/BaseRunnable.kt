package net.drabc.INSWatchDog.Runnable

import kotlinx.coroutines.delay
import net.drabc.INSWatchDog.Logger
import net.drabc.INSWatchDog.RconClient.RconClient
import net.drabc.INSWatchDog.Vars.Var

open class BaseRunnable(_delayTime: Int = Var.settingBase.setting.waitTime) {
    private var delayTime = 0
    init {
        delayTime = _delayTime
    }
    open fun execute(client: RconClient){
        //Dummy
    }
    suspend fun run(client: RconClient) {
        Var.logger.log("启动${Integer.toHexString(System.identityHashCode(this))}协程",
            Logger.LogType.WARN
        )
        while (true) {
            execute(client)
            delay((delayTime * 1000).toLong())
        }
    }
}