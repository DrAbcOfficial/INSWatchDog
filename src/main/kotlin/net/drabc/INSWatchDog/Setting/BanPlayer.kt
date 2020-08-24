package net.drabc.INSWatchDog.Setting

data class BanPlayer(
    val id: Long,
    val reason: String
){
    override fun equals(other: Any?): Boolean {
        if(other == null)
            return false
        if(other !is BanPlayer)
            return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + reason.hashCode()
        return result
    }
}