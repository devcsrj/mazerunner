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
 * @param G the grid type
 */
interface MazeGenerator<G> {

    /**
     * @return the grid this generator operates upon
     */
    fun getGrid(): G

    /**
     * Creates a maze starting at the grid cell `(x, y)`.
     *
     * @param x
     * x-coordinate (column) of start cell
     * @param y
     * y-coordinate (row) of start cell
     * @return maze (spanning tree)
     */
    fun createMaze(x: Int, y: Int): G
}
