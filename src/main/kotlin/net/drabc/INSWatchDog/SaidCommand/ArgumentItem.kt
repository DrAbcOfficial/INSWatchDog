package net.drabc.INSWatchDog.SaidCommand

data class ArgumentItem(val name: String, val optional: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is ArgumentItem)
            return false
        return this.name == other.name
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + optional.hashCode()
        return result
    }
}