package mazerunner

import java.time.Duration
import java.time.LocalDateTime

/**
 * A runner implementation that limits the lifespan of this runner after a
 * specified duration
 */
class EphemeralMazeRunner(private val delegate: MazeRunner,
                          private val lifespan: Duration) : MazeRunner {

    private var started: LocalDateTime? = null

    override fun tag() = delegate.tag()

    override fun move(destination: Point): Position {
        if (started == null) {
            started = LocalDateTime.now()
        }

        val max = started!!.plus(lifespan)
        val lived = LocalDateTime.now()
        require(lived.isBefore(max)) {
            "Runner '${tag()}' exceeded lifespan of $lifespan"
        }
        return delegate.move(destination)
    }

    override fun jump() = delegate.jump()

    override fun steps() = delegate.steps()

    override fun toString() = "Ephemeral[$delegate]"
}
