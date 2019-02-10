package mazerunner

import org.springframework.core.io.buffer.DataBuffer

data class MazeMovementEvent(val runner: MazeRunner,
                             val type: Type,
                             val message: String) {

    enum class Type {
        MOVED,
        FAILED
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

    buffer.write("id=${this.runner.tag().id};".toByteArray())
    buffer.write("name=${this.runner.tag().name};".toByteArray())

    val position = this.runner.jump()
    buffer.write("position=".toByteArray())
    position.writeTo(buffer)
    buffer.write(';'.toByte())

    buffer.write("message=${this.message}".toByteArray())
}
