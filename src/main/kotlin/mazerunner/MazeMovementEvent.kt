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
