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
