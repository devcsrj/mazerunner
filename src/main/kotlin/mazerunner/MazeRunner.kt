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

/**
 * Interface for moving a character around the maze
 */
interface MazeRunner {

    /**
     * @return the identifier for this runner
     */
    fun tag(): Tag

    /**
     * Moves the [MazeRunner] to the [destination].
     *
     * When [destination] is equal to (-1, -1), this function returns the current position
     *
     * @return the resulting position
     */
    fun move(destination: Point): Position

    /**
     * @return the current position
     */
    fun jump(): Position

    /**
     * @return the number of steps made by this runner
     */
    fun steps(): Long
}
