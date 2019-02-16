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

import de.amr.easy.data.Partition
import de.amr.easy.graph.api.traversal.TraversalState
import de.amr.easy.grid.impl.OrthogonalGrid
import de.amr.easy.grid.impl.OrthogonalGrid.emptyGrid
import de.amr.easy.grid.impl.OrthogonalGrid.fullGrid
import de.amr.easy.util.StreamUtils.permute


class KruskalMazeGenerator(numCols: Int,
                           numRows: Int) : MazeGenerator<OrthogonalGrid> {

    private var grid: OrthogonalGrid = emptyGrid(numCols, numRows, TraversalState.UNVISITED)

    override fun getGrid(): OrthogonalGrid {
        return grid
    }

    override fun createMaze(x: Int, y: Int): OrthogonalGrid {
        val forest = Partition<Int>()
        val edges = fullGrid(grid.numCols(), grid.numRows(), TraversalState.UNVISITED).edges()
        permute(edges).forEach { edge ->
            val u = edge.either()
            val v = edge.other()
            if (forest.find(u) !== forest.find(v)) {
                grid.addEdge(u, v)
                grid.set(u, TraversalState.COMPLETED)
                grid.set(v, TraversalState.COMPLETED)
                forest.union(u, v)
            }
        }
        return grid
    }
}
