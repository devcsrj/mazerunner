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

import de.amr.easy.graph.api.traversal.TraversalState
import de.amr.easy.grid.impl.OrthogonalGrid.emptyGrid
import de.amr.easy.grid.impl.OrthogonalGrid.fullGrid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class GridMazeRunnerTest {

    @Test
    fun `can move point to point`() {
        val ninja = Tag("devcsrj", "RJ")
        val maze = fullGrid(2, 1, TraversalState.UNVISITED)
        val start = Point(0, 0)

        val runner = GridMazeRunner(ninja, maze, start)
        // maze: move left to right
        val actual = runner.move(Point(1, 0))
        assertEquals(Position(
                current = Point(1, 0),
                neighbors = arrayOf(Point(0, 0))), actual)

        maze.vertices().forEach { assertEquals(runner.getState(it), TraversalState.VISITED) }
    }

    @Test
    fun `cant move to unconnected vertex`() {
        val ninja = Tag("devcsrj", "RJ")
        val maze = emptyGrid(2, 1, TraversalState.UNVISITED)
        val start = Point(0, 0)

        val runner = GridMazeRunner(ninja, maze, start)
        // maze: move left to right
        try {
            val actual = runner.move(Point(1, 0))
            fail()
        } catch (ex: IllegalArgumentException) {
            assertEquals("It's impossible for RJ to move from (0, 0) to (1, 0)", ex.message)
        }

        assertEquals(runner.getState(0), TraversalState.VISITED)
        assertEquals(runner.getState(1), TraversalState.UNVISITED)
    }

    @Test
    fun `can get current position with (-1, -1)`() {
        val ninja = Tag("devcsrj", "RJ")
        val maze = emptyGrid(1, 1, TraversalState.UNVISITED)
        val start = Point(0, 0)

        val runner = GridMazeRunner(ninja, maze, start)

        val actual = runner.move(Point(-1, -1))
        assertEquals(Position(start, emptyArray()), actual)
    }
}
