package mazerunner

import mazerunner.Leaderboard.Entry
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.TopicProcessor
import reactor.core.publisher.toFlux
import java.util.concurrent.ConcurrentHashMap

class MazeLeaderboard(eventPublisher: TopicProcessor<MazeMovementEvent>,
                      goal: Point) : Leaderboard {

    private val logger = LoggerFactory.getLogger(MazeLeaderboard::class.java)
    private val scores = ConcurrentHashMap<Tag, Entry>()

    init {
        eventPublisher.filter { it.runner.jump().current == goal }
                .map { it.runner }
                .doOnNext { scores[it.tag()] = Entry(it.tag(), it.steps()) }
                .subscribe {
                    logger.info("Runner '$it' reached goal '$goal' after ${it.steps()} move(s)")
                }
    }

    override fun scores(): Flux<Entry> = scores.values
            .sortedBy { it.moves }
            .toFlux()
            .take(20L)
}
