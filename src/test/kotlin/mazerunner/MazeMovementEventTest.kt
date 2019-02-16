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
        val runner = FlyingMazeRunner(tag, position)
        val event = MazeMovementEvent(runner, MazeMovementEvent.Type.MOVED, "OK")

        val factory = DefaultDataBufferFactory()
        val buffer = factory.allocateBuffer()
        event.writeTo(buffer)

        val actual = readAsString(buffer)
        assertEquals("[MOVED]id=devcsrj;name=RJ;position=(1,1)[(1,0),(0,1)];message=OK", actual)
    }

    private fun readAsString(buffer: DataBuffer) = String(buffer.asInputStream(true).readBytes())

}
