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

import java.util.concurrent.atomic.LongAdder

/**
 * A runner that can move anywhere
 *
 * Mainly used for testing
 */
class FlyingMazeRunner(private val tag: Tag,
                       private val start: Position) : MazeRunner {

    private var current: Position = start
    private val steps = LongAdder()

    override fun tag() = tag

    override fun move(destination: Point): Position {
        current = Position(destination, arrayOf())
        steps.increment()
        return current
    }

    override fun jump() = current

    override fun steps() = steps.sum()

    override fun toString() = "Flying[$tag]"
}
