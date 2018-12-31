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
            assertEquals("It's impossible for RJ to move from cell '0' to '1'", ex.message)
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
