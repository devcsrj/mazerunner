package mazerunner

import de.amr.easy.grid.api.GridPosition
import de.amr.easy.grid.impl.OrthogonalGrid
import org.springframework.core.io.buffer.DataBuffer

data class Point(val x: Int,
                 val y: Int)

/**
 * Serializes this point into: (x,y)
 */
fun Point.writeTo(buffer: DataBuffer) = buffer.write("(${this.x},${this.y})".toByteArray())
fun OrthogonalGrid.cellOn(point: Point) = this.cell(point.x, point.y)
fun OrthogonalGrid.pointOf(cell: Int) = Point(this.col(cell), this.row(cell))
fun OrthogonalGrid.pointOf(position: GridPosition) = pointOf(cell(position))


