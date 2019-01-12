package mazerunner

import org.springframework.core.io.buffer.DataBuffer

data class Point(val x: Int,
                 val y: Int)

/**
 * Serializes this point into: (x,y)
 */
fun Point.writeTo(buffer: DataBuffer) = buffer.write("(${this.x},${this.y})".toByteArray())
