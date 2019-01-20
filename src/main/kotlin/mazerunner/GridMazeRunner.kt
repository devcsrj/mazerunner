package mazerunner

import de.amr.easy.graph.api.traversal.TraversalState
import de.amr.easy.graph.impl.traversal.ObservableGraphTraversal
import de.amr.easy.grid.impl.OrthogonalGrid
import org.slf4j.LoggerFactory

/**
 * An implementation of [MazeRunner] that operates on an [OrthogonalGrid]
 */
class GridMazeRunner(private val tag: Tag,
                     private val grid: OrthogonalGrid,
                     start: Point) : MazeRunner, ObservableGraphTraversal() {

    private val logger = LoggerFactory.getLogger(GridMazeRunner::class.java)
    private val nowhere = Point(-1, -1)
    private var currentCell: Int? = null

    init {
        currentCell = cellOn(start)
        goTo(start)
        logger.info("${tag.name} starts at cell '$currentCell'")
    }

    override fun tag() = tag

    override fun traverseGraph(source: Int) = throw UnsupportedOperationException()

    override fun traverseGraph(source: Int, target: Int) {
        require(currentCell == source) {
            "${tag.name} is currently on cell '$currentCell', and not on $source"
        }
        goTo(pointOf(target))
    }

    override fun move(destination: Point): Position {
        if (destination == nowhere)
            return currentPosition()
        goTo(destination)
        return currentPosition()
    }

    private fun cellOn(point: Point) = grid.cell(point.x, point.y)

    private fun pointOf(cell: Int) = Point(grid.col(cell), grid.row(cell))

    private fun goTo(point: Point) {
        val srcCell = currentCell!!
        val destCell = cellOn(point)
        if (srcCell != destCell)
            require(grid.adjacent(srcCell, destCell)) {
                val from = pointOf(srcCell)
                val to = pointOf(destCell)
                "It's impossible for ${tag.name} to move from (${from.x}, ${from.y}) " +
                        "to (${to.x}, ${to.y})"
            }
        setState(destCell, TraversalState.VISITED)
        currentCell = destCell
    }

    private fun currentPosition() = Position(
            current = pointOf(currentCell!!),
            neighbors = neighborsOf(currentCell!!))

    private fun neighborsOf(cell: Int) = grid
            .neighbors(cell)
            .filter { grid.adjacent(it, cell) }
            .mapToObj { pointOf(it) }
            .toArray<Point> { length -> arrayOfNulls(length) }
}
