package mazerunner

import de.amr.easy.grid.impl.OrthogonalGrid
import java.time.Duration
import java.util.function.Supplier

/**
 * Creates a [GridMazeRunner]
 */
class GridMazeRunnerFactory(private val maze: OrthogonalGrid,
                            private val startPoint: Supplier<Point>,
                            private val lifespan: Duration) : MazeRunnerFactory {

    override fun create(tag: Tag): MazeRunner {
        val runner = GridMazeRunner(tag, maze, startPoint.get())
        return EphemeralMazeRunner(runner, lifespan)
    }
}
