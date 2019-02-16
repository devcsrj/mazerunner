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


