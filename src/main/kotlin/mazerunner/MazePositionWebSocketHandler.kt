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

import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.DirectProcessor
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
        val processor = DirectProcessor.create<MazeMovementEvent>()
        val subscription = eventPublisher
                .doOnNext { processor.onNext(it) }
                .doOnError { processor.onError(it) }
                .doOnComplete { processor.onComplete() }
                .subscribe()

        session.receive().doFinally {
            subscription.dispose()
        }.subscribe()

        return session.send(processor.map { event ->
            val buffer = dataBufferFactory.allocateBuffer()
            event.writeTo(buffer)
            WebSocketMessage(WebSocketMessage.Type.TEXT, buffer)
        })
    }
}
