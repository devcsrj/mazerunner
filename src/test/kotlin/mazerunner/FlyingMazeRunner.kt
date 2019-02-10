package mazerunner

import java.util.concurrent.atomic.LongAdder

/**
 * A runner that can move anywhere
 *
 * Mainly used for testing
 */
class FlyingMazeRunner(private val tag: Tag,
                       private val start: Position) : MazeRunner {

    private var current: Position = start
    private val steps = LongAdder()

    override fun tag() = tag

    override fun move(destination: Point): Position {
        current = Position(destination, arrayOf())
        steps.increment()
        return current
    }

    override fun jump() = current

    override fun steps() = steps.sum()

    override fun toString() = "Flying[$tag]"
}
