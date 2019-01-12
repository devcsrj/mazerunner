package mazerunner

import org.junit.Test
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import kotlin.test.assertEquals

class MazeMovementEventTest {

    @Test
    fun `can write event to buffer`() {
        val position = Position(Point(1, 1),
                neighbors = arrayOf(
                        Point(1, 0),
                        Point(0, 1)))
        val tag = Tag("devcsrj", "RJ")
        val event = MazeMovementEvent(tag, MazeMovementEvent.Type.MOVED, position)

        val factory = DefaultDataBufferFactory()
        val buffer = factory.allocateBuffer()
        event.writeTo(buffer)

        val actual = readAsString(buffer)
        assertEquals("[MOVED]id=devcsrj;name=RJ;position=(1,1)[(1,0),(0,1)]", actual)
    }

    private fun readAsString(buffer: DataBuffer) = String(buffer.asInputStream(true).readBytes())

}
