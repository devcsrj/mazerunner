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

import org.junit.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.ReplayProcessor
import reactor.core.publisher.TopicProcessor
import reactor.test.StepVerifier

class MazeLeaderboardTest {

    @Test
    fun `can get scores in descending order`() {
        val goal = Point(9, 9) // Nine-nine!
        val topic = TopicProcessor.create<MazeMovementEvent>()
        val leaderboard = MazeLeaderboard(topic, goal)

        val dora = Tag("dora-the-explorer", "Dora")
        val indiana = Tag("indiana-jones", "Indiana")
        val columbus = Tag("christopher-columbus", "Chris")

        val replay = ReplayProcessor.create<MazeMovementEvent>(3)

        Flux.concat(
                generateMoves(columbus, goal, 50),
                generateMoves(dora, goal, 3),
                generateMoves(indiana, goal, 25))
                .subscribeWith(topic)
        StepVerifier.create(replay
                .subscribeWith(topic)
                .thenMany(leaderboard.scores()))
                .expectNext(Leaderboard.Entry(dora, 3))
                .expectNext(Leaderboard.Entry(indiana, 25))
                .expectNext(Leaderboard.Entry(columbus, 50))
                .verifyComplete()
    }

    private fun generateMoves(tag: Tag, goal: Point, count: Long): Flux<MazeMovementEvent> {
        val runner = FlyingMazeRunner(tag, Position(Point(-1, -1), arrayOf()))
        return Flux.generate<MazeMovementEvent> { it.next(MazeMovementEvent(runner, MazeMovementEvent.Type.MOVED, "OK")) }
                .doOnEach { runner.move(Point(-1, -1)) } // Fake a movement
                .take(count - 1)
                .thenMany(Flux.just(runner)
                        .doOnNext { runner.move(goal) } // Finish!
                        .map { MazeMovementEvent(runner, MazeMovementEvent.Type.MOVED, "Finished") })
    }
}
