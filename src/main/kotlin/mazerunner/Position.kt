package mazerunner

import org.springframework.core.io.buffer.DataBuffer

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

/**
 * Serializes this object as: (x,y)[(x1,y1)...(xn,yn)]
 */
fun Position.writeTo(buffer: DataBuffer)  {
    this.current.writeTo(buffer)
    if (this.neighbors.isEmpty()) {
        buffer.write("[]".toByteArray())
        return
    }

    buffer.write('['.toByte())
    for (p in this.neighbors) {
        p.writeTo(buffer)
        buffer.write(','.toByte())
    }
    buffer.writePosition(buffer.writePosition() - 1) // move one backwards
    buffer.write(']'.toByte())
}
