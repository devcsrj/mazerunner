package mazerunner

import reactor.core.publisher.Flux

interface Leaderboard {

    /**
     * @return the scores in descending order
     */
    fun scores(): Flux<Entry>

    data class Entry(val tag: Tag,
                     val moves: Long)
}
