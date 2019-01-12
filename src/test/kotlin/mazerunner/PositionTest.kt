package mazerunner

import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory

class PositionTest {

    @Test
    fun `can serialize position`() {
        val factory = DefaultDataBufferFactory()

        var buffer = factory.allocateBuffer()
        Position(Point(0, 0), arrayOf()).writeTo(buffer)
        var written = readAsString(buffer)
        assertEquals("(0,0)[]", written)

        buffer = factory.allocateBuffer()
        Position(Point(0, 0), arrayOf(Point(1, 0))).writeTo(buffer)
        written = readAsString(buffer)
        assertEquals("(0,0)[(1,0)]", written)

        buffer = factory.allocateBuffer()
        Position(Point(0, 0), arrayOf(Point(1, 0), Point(0, 1))).writeTo(buffer)
        written = readAsString(buffer)
        assertEquals("(0,0)[(1,0),(0,1)]", written)
    }

    private fun readAsString(buffer: DataBuffer) = String(buffer.asInputStream(true).readBytes())
}
