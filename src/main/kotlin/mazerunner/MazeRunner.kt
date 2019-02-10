package mazerunner

/**
 * Interface for moving a character around the maze
 */
interface MazeRunner {

    /**
     * @return the identifier for this runner
     */
    fun tag(): Tag

    /**
     * Moves the [MazeRunner] to the [destination].
     *
     * When [destination] is equal to (-1, -1), this function returns the current position
     *
     * @return the resulting position
     */
    fun move(destination: Point): Position

    /**
     * @return the current position
     */
    fun jump(): Position

    /**
     * @return the number of steps made by this runner
     */
    fun steps(): Long
}
