package mazerunner

import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory

class PointTest {

    @Test
    fun `can write point to buffer`() {
        val factory = DefaultDataBufferFactory()
        val buffer = factory.allocateBuffer()
        val point = Point(2, 3)
        point.writeTo(buffer)

        val actual = readAsString(buffer)
        assertEquals("(2,3)", actual)
    }

    private fun readAsString(buffer: DataBuffer) = String(buffer.asInputStream(true).readBytes())
}
