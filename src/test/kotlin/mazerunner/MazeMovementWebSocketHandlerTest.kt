package mazerunner

import mazerunner.MazeMovementWebSocketHandler.Companion.extractPoint
import mazerunner.MazeMovementWebSocketHandler.Companion.writePosition
import org.junit.Test
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import kotlin.test.assertEquals

class MazeMovementWebSocketHandlerTest {

    @Test
    fun `can transform coordinates to Points`() {
        var actual = extractPoint("(0,0)")
        assertEquals(actual, Point(0, 0))

        actual = extractPoint("(0,1)")
        assertEquals(actual, Point(0, 1))

        actual = extractPoint("(1,1)")
        assertEquals(actual, Point(1, 1))

        actual = extractPoint("(-1,-1)")
        assertEquals(actual, Point(-1, -1))
    }

    @Test
    fun `can serialize position`() {
        val factory = DefaultDataBufferFactory()

        var buffer = factory.allocateBuffer()
        writePosition(Position(Point(0, 0), arrayOf()), buffer)
        var written = readAsString(buffer)
        assertEquals("(0,0)[]", written)

        buffer = factory.allocateBuffer()
        writePosition(Position(Point(0, 0), arrayOf(Point(1, 0))), buffer)
        written = readAsString(buffer)
        assertEquals("(0,0)[(1,0)]", written)

        buffer = factory.allocateBuffer()
        writePosition(Position(Point(0, 0), arrayOf(Point(1, 0), Point(0, 1))), buffer)
        written = readAsString(buffer)
        assertEquals("(0,0)[(1,0),(0,1)]", written)
    }

    private fun readAsString(buffer: DataBuffer) = String(buffer.asInputStream(true).readBytes())
}
