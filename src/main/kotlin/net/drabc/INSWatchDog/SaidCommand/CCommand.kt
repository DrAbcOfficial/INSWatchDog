package net.drabc.INSWatchDog.SaidCommand

data class CCommand(private val raw: List<String>){
    fun argC(): Int{
        return raw.size
    }
    fun getCommandString(): String{
        var tempString: String = ""
        raw.forEach { tempString += "$it " }
        return tempString
    }
    fun getArgumentsString():String{
        var tempString: String = ""
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