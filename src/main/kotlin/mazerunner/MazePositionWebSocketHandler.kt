package mazerunner

import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.TopicProcessor

/**
 * Broadcasts [Position]s sent by [MazeMovementWebSocketHandler]
 *
 * Writes payload in the form described by [MazeMovementEvent.writeTo]
 */
class MazePositionWebSocketHandler(private val eventPublisher: TopicProcessor<MazeMovementEvent>) : WebSocketHandler {

    private val dataBufferFactory = DefaultDataBufferFactory()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val subscription = eventPublisher.subscribe { event ->
            session.send {
                val buffer = dataBufferFactory.allocateBuffer()
                event.writeTo(buffer)
                WebSocketMessage(WebSocketMessage.Type.TEXT, buffer)
            }.subscribe()
        }
        return Mono.never<Void>().doOnTerminate {
            subscription.dispose()
        }
    }
}
