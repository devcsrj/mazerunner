package mazerunner

import de.amr.easy.grid.api.GridPosition
import de.amr.easy.grid.impl.OrthogonalGrid
import java.util.function.Supplier

/**
 * Creates a [MazeRunner] that randomly puts the runner on any corner of the maze
 *
 * The goal is always the center cell
 */
class GridMazeRunnerFactory(val maze: OrthogonalGrid,
                            val startPosition: Supplier<GridPosition>) : MazeRunnerFactory {

    override fun create(tag: Tag) = GridMazeRunner(tag, maze, nextStartPoint())

    private fun nextStartPoint(): Point {
        val position = startPosition.get()
        val cell = maze.cell(position)
        return Point(maze.col(cell), maze.row(cell))
    }
}
