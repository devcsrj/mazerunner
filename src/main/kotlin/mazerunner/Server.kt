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

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import de.amr.easy.grid.api.GridPosition
import de.amr.easy.grid.impl.OrthogonalGrid
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues
import java.io.InputStreamReader
import java.net.URI
import java.time.Duration
import java.util.function.Function
import java.util.function.Supplier


@SpringBootApplication
open class Server {

    @Bean
    open fun activeMaze(props: MazeProperties) = KruskalMazeGenerator(props.columns, props.rows).createMaze(0, 0)

    @Bean
    open fun goalPoint(activeMaze: OrthogonalGrid,
                       props: MazeProperties): Supplier<Point> {
        val topRight = activeMaze.pointOf(GridPosition.TOP_RIGHT)
        val bottomRight = activeMaze.pointOf(GridPosition.BOTTOM_RIGHT)
        val start = Point(
                ((topRight.x + bottomRight.x) / 2) - 1,
                (topRight.y + bottomRight.y) / 2)
        return Supplier { start }
    }

    @Bean
    open fun startPoint(activeMaze: OrthogonalGrid,
                        props: MazeProperties): Supplier<Point> {
        val topLeft = activeMaze.pointOf(GridPosition.TOP_LEFT)
        return Supplier { topLeft }
    }

    @Bean
    open fun mazeRunnerService(activeMaze: OrthogonalGrid,
                               startPoint: Supplier<Point>,
                               props: RunnerProperties): MazeRunnerFactory {
        val lifespan = Duration.ofMillis(props.lifespan)
        return GridMazeRunnerFactory(activeMaze, startPoint, lifespan)
    }

    @Bean
    open fun tagFunction() = ExtractTagFromSessionHeader("x-runner-tag")

    @Bean
    open fun positionTopicProcessor(): TopicProcessor<MazeMovementEvent> {
        return TopicProcessor
                .builder<MazeMovementEvent>()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(true)
                .build()
    }

    @Bean
    open fun leaderboard(positionTopicProcessor: TopicProcessor<MazeMovementEvent>,
                         goalPoint: Supplier<Point>): Leaderboard {
        return MazeLeaderboard(positionTopicProcessor, goalPoint.get())
    }

    @Bean
    open fun mazeMovementWebSocketHandler(mazeRunnerFactory: MazeRunnerFactory,
                                          tagFunction: Function<WebSocketSession, Tag?>,
                                          positionTopicProcessor: TopicProcessor<MazeMovementEvent>): WebSocketHandler {
        return MazeMovementWebSocketHandler(mazeRunnerFactory, tagFunction, positionTopicProcessor)
    }

    @Bean
    open fun mazePositionWebSocketHandler(positionTopicProcessor: TopicProcessor<MazeMovementEvent>): WebSocketHandler {
        return MazePositionWebSocketHandler(positionTopicProcessor)
    }

    @Bean
    open fun webSocketHandlerMapping(
            mazeMovementWebSocketHandler: WebSocketHandler,
            mazePositionWebSocketHandler: WebSocketHandler): SimpleUrlHandlerMapping {

        val mapping = SimpleUrlHandlerMapping()
        mapping.order = 1000
        mapping.urlMap = mapOf(
                "/maze/move" to mazeMovementWebSocketHandler,
                "/maze/positions" to mazePositionWebSocketHandler)
        return mapping
    }

    @Bean
    open fun webSocketHandlerAdapter() = WebSocketHandlerAdapter()

    @Bean
    open fun routes(props: MazeProperties,
                    leaderboard: Leaderboard,
                    goalPoint: Supplier<Point>) = router {
        GET("/maze/info") {
            val info = "{\"columns\":${props.columns},\"rows\":${props.rows}}"
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(info)
        }
        GET("/maze/goal") {
            val (x, y) = goalPoint.get()
            val info = "{\"x\":$x,\"y\":$y}"
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(info)
        }
        GET("/maze/scores") {
            ServerResponse.ok().body(leaderboard.scores(), Leaderboard.Entry::class.java)
        }
        GET("/maze") {
            ServerResponse.temporaryRedirect(URI.create("/index.html")).build()
        }
        GET("/") { request ->
            val options = MutableDataSet()
            options.set(HtmlRenderer.SOFT_BREAK, "<br />\n")

            val parser = Parser.builder(options).build()
            val renderer = HtmlRenderer.builder(options).build()

            val resource = javaClass.getResourceAsStream("/scripture.md")
            val document = InputStreamReader(resource).use {
                parser.parseReader(it)
            }
            ServerResponse.ok()
                    .header("Content-Type", "text/html")
                    .syncBody(renderer.render(document))
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Server::class.java, *args)
}
