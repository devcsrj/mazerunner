/**
 * Copyright © 2018 Reijhanniel Jearl Campos (devcsrj@torocloud.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
