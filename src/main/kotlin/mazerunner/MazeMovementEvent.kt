package mazerunner

import org.springframework.core.io.buffer.DataBuffer

data class MazeMovementEvent(val tag: Tag,
                             val type: Type,
                             val position: Position?) {

    constructor(tag: Tag, type: Type) : this(tag, type, null)

    enum class Type {
        MOVED,
        FAILED,
        FINISHED
    }

}

/**
 * Writes to buffer in the form of:
 * ```
 * [MOVED]id=devcsrj;name=RJ;position=(1,1)[(1,0),(0,1)]
 * ```
 */
fun MazeMovementEvent.writeTo(buffer: DataBuffer) {
    buffer.write('['.toByte())
    buffer.write(this.type.name.toByteArray())
    buffer.write(']'.toByte())

    buffer.write("id=${this.tag.id};".toByteArray())
    buffer.write("name=${this.tag.name};".toByteArray())

    if (this.position != null) {
        buffer.write("position=".toByteArray())
        position.writeTo(buffer)
    }
}
