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

import java.time.Duration
import java.time.LocalDateTime

/**
 * A runner implementation that limits the lifespan of this runner after a
 * specified duration
 */
class EphemeralMazeRunner(private val delegate: MazeRunner,
                          private val lifespan: Duration) : MazeRunner {

    private var started: LocalDateTime? = null

    override fun tag() = delegate.tag()

    override fun move(destination: Point): Position {
        if (started == null) {
            started = LocalDateTime.now()
        }

        val max = started!!.plus(lifespan)
        val lived = LocalDateTime.now()
        require(lived.isBefore(max)) {
            "Runner '${tag()}' exceeded lifespan of $lifespan"
        }
        return delegate.move(destination)
    }

    override fun jump() = delegate.jump()

    override fun steps() = delegate.steps()

    override fun toString() = "Ephemeral[$delegate]"
}
