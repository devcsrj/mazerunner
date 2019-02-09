package mazerunner

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
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.TopicProcessor
import reactor.util.concurrent.Queues
import java.util.function.Function
import java.util.function.Supplier


@SpringBootApplication
open class Server {

    @Bean
    open fun activeMaze(props: MazeProperties) = KruskalMazeGenerator(props.columns, props.rows).createMaze(0, 0)

    @Bean
    open fun goal(activeMaze: OrthogonalGrid,
                  props: MazeProperties): Supplier<Point> {
        val topRight = activeMaze.cell(GridPosition.TOP_RIGHT) - 1
        val bottomRight = activeMaze.cell(GridPosition.BOTTOM_RIGHT) - 1
        val start = (topRight + bottomRight) / 2
        return Supplier { activeMaze.pointOf(start) }
    }

    @Bean
    open fun startPoint(activeMaze: OrthogonalGrid,
                        props: MazeProperties): Supplier<Point> {
        val topLeft = activeMaze.cell(GridPosition.TOP_LEFT)
        val bottomLeft = activeMaze.cell(GridPosition.BOTTOM_LEFT)
        val start = (topLeft + bottomLeft) / 2
        return Supplier { activeMaze.pointOf(start) }
    }

    @Bean
    open fun mazeRunnerService(activeMaze: OrthogonalGrid,
                               startPoint: Supplier<Point>) = GridMazeRunnerFactory(activeMaze, startPoint)

    @Bean
    open fun tagFunction() = ExtractTagFromSessionHeader("x-runner-tag")

    @Bean
    open fun positionTopicProcessor(): TopicProcessor<MazeMovementEvent>  {
        val topic = TopicProcessor
                .builder<MazeMovementEvent>()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(true)
                .build()

        // There must be at least one consumer for the topic to continue publishing
        val blindConsumer = DirectProcessor.create<MazeMovementEvent>()
        topic.subscribeWith(blindConsumer)
        blindConsumer.subscribe()

        return topic
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
                    goal: Supplier<Point>) = router {
        GET("/maze/info") {
            val info = "{\"columns\":${props.columns},\"rows\":${props.rows}}"
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(info)
        }
        GET("/maze/goal") {
            val (x, y) = goal.get()
            val info = "{\"x\":$x,\"y\":$y}"
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(info)
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Server::class.java, *args)
}
