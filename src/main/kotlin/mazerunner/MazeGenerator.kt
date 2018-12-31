package mazerunner

/**
 * @param G the grid type
 */
interface MazeGenerator<G> {

    /**
     * @return the grid this generator operates upon
     */
    fun getGrid(): G

    /**
     * Creates a maze starting at the grid cell `(x, y)`.
     *
     * @param x
     * x-coordinate (column) of start cell
     * @param y
     * y-coordinate (row) of start cell
     * @return maze (spanning tree)
     */
    fun createMaze(x: Int, y: Int): G
}
