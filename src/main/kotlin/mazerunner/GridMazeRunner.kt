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
import de.amr.easy.graph.impl.traversal.ObservableGraphTraversal
import de.amr.easy.grid.impl.OrthogonalGrid
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.LongAdder

/**
 * An implementation of [MazeRunner] that operates on an [OrthogonalGrid]
 */
class GridMazeRunner(private val tag: Tag,
                     private val grid: OrthogonalGrid,
                     private val start: Point) : MazeRunner, ObservableGraphTraversal() {

    private val logger = LoggerFactory.getLogger(GridMazeRunner::class.java)
    private val nowhere = Point(-1, -1)
    private val steps = LongAdder()
    private var currentCell: Int? = null

    init {
        currentCell = grid.cellOn(start)
        goTo(start)
        logger.info("${tag.name} starts at cell '$currentCell'")
    }

    override fun tag() = tag

    override fun traverseGraph(source: Int) = throw UnsupportedOperationException()

    override fun traverseGraph(source: Int, target: Int) {
        require(currentCell == source) {
            "${tag.name} is currently on cell '$currentCell', and not on $source"
        }
        goTo(grid.pointOf(target))
    }

    override fun move(destination: Point): Position {
        if (destination == nowhere)
            return currentPosition()
        goTo(destination)
        steps.increment()
        return currentPosition()
    }

    override fun jump() = currentPosition()

    override fun steps() = steps.sum()

    override fun toString() = "Runner[$tag]"

    private fun goTo(point: Point) {
        val srcCell = currentCell!!
        val destCell = grid.cellOn(point)
        if (srcCell != destCell)
            require(grid.adjacent(srcCell, destCell)) {
                val from = grid.pointOf(srcCell)
                val to = grid.pointOf(destCell)
                "It's impossible for ${tag.name} to move from (${from.x}, ${from.y}) " +
                        "to (${to.x}, ${to.y})"
            }
        setState(destCell, TraversalState.VISITED)
        currentCell = destCell
    }

    private fun currentPosition() = Position(
            current = grid.pointOf(currentCell!!),
            neighbors = neighborsOf(currentCell!!))

    private fun neighborsOf(cell: Int) = grid
            .neighbors(cell)
            .filter { grid.adjacent(it, cell) }
            .mapToObj { grid.pointOf(it) }
            .toArray<Point> { length -> arrayOfNulls(length) }
}
