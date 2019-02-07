package mazerunner

import de.amr.easy.grid.impl.OrthogonalGrid
import java.util.function.Supplier

/**
 * Creates a [GridMazeRunner]
 */
class GridMazeRunnerFactory(private val maze: OrthogonalGrid,
                            private val startPoint: Supplier<Point>) : MazeRunnerFactory {

    override fun create(tag: Tag) = GridMazeRunner(tag, maze, startPoint.get())
}
