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
