package mazerunner

/**
 * Central service for managing [MazeRunner]s
 */
interface MazeRunnerFactory {

    /**
     * Loads the [MazeRunner] associated with [tag]
     *
     * @return the [MazeRunner]
     */
    fun create(tag: Tag): MazeRunner?
}
