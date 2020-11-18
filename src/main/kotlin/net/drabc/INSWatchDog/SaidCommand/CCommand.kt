package net.drabc.inswatchdog.saidcommand

data class CCommand(private val raw: List<String>){
    fun argC(): Int{
        return raw.size
    }
    fun getCommandString(): String{
        var tempString = ""
        raw.forEach { tempString += "$it " }
        return tempString
    }
    fun getArgumentsString():String{
        var tempString = ""
        raw.takeLast(raw.size - 1).forEach { tempString += "$it " }
        return tempString
    }
    fun arg(iIndex: Int): String{
        return raw[iIndex]
    }
    operator fun get(i: Int): String {
        return raw[i]
    }
}