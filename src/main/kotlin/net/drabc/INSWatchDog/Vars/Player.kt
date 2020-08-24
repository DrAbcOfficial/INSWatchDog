package net.drabc.INSWatchDog.Vars

data class Player (
    var ID: Int = 0,
    var Name: String = "",
    var NetID: Long = 0,
    var IP: String = "",
    var Score: Long = 0
){
    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false
        if(other !is Player)
            return false
        return this.NetID == other.NetID
    }

    override fun hashCode(): Int {
        var result = ID
        result = 31 * result + Name.hashCode()
        result = 31 * result + NetID.hashCode()
        result = 31 * result + IP.hashCode()
        result = 31 * result + Score.hashCode()
        return result
    }
}