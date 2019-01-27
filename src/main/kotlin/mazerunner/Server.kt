package mazerunner

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
import java.util.function.Function


@SpringBootApplication
open class Server {

    @Bean
    open fun activeMaze(props: MazeProperties) = KruskalMazeGenerator(props.columns, props.rows).createMaze(0, 0)

    @Bean
    open fun mazeRunnerService(activeMaze: OrthogonalGrid) = GridMazeRunnerFactory(activeMaze)

    @Bean
    open fun tagFunction() = ExtractTagFromSessionHeader("x-runner-tag")

    @Bean
    open fun positionTopicProcessor(): TopicProcessor<MazeMovementEvent> = TopicProcessor
            .builder<MazeMovementEvent>()
            .bufferSize(Queues.SMALL_BUFFER_SIZE)
            .share(true)
            .autoCancel(true)
            .build()

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
                    activeMaze: OrthogonalGrid) = router {
        GET("/maze/info") {
            val info = "{\"columns\":${props.columns},\"rows\":${props.rows}}"
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(info)
        }
        GET("/maze/goal") {
            val point = activeMaze.centerPoint()
            val info = "{\"x\":${point.x},\"y\":${point.y}}"
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(info)
        }
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Server::class.java, *args)
}
