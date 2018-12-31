package mazerunner

import de.amr.easy.graph.api.traversal.TraversalState
import de.amr.easy.grid.impl.OrthogonalGrid
import de.amr.easy.grid.impl.OrthogonalGrid.fullGrid

/**
 * A generator that interconnects all adjacent vertices
 */
class OpenMazeGenerator(numCols: Int,
                        numRows: Int) : MazeGenerator<OrthogonalGrid> {

    private var grid: OrthogonalGrid = fullGrid(numCols, numRows, TraversalState.UNVISITED)

    override fun getGrid() = grid
    override fun createMaze(x: Int, y: Int) = grid

}
