package mazerunner

data class Position(val current: Point,
                    val neighbors: Array<Point>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (current != other.current) return false
        if (!neighbors.contentEquals(other.neighbors)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = current.hashCode()
        result = 31 * result + neighbors.contentHashCode()
        return result
    }

}
