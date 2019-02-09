package mazerunner

import de.amr.easy.grid.api.GridPosition
import de.amr.easy.grid.impl.OrthogonalGrid
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.StandardWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.ReplayProcessor
import java.net.URI
import java.net.URISyntaxException
import java.time.Duration
import java.util.function.Supplier
import kotlin.test.assertEquals


@RunWith(SpringRunner::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [ServerTest.Config::class])
class ServerTest {

    @Import(Server::class)
    open class Config {

        @Bean
        open fun activeMaze() = OpenMazeGenerator(3, 1).createMaze(0, 0)

        @Bean
        open fun mazeRunnerService(activeMaze: OrthogonalGrid) =
                GridMazeRunnerFactory(activeMaze, Supplier{ Point(0, 0)}, Duration.ofSeconds(5L))
    }

    @LocalServerPort
    private val port: String? = null

    @Throws(URISyntaxException::class)
    private fun getUrl(path: String): URI {
        return URI("ws://localhost:" + this.port + path)
    }

    @Test
    fun `can solve maze over websocket`() {
        val moves = Flux.just("(0,0)", "(1,0)")
        val headers = HttpHeaders()
        headers.add("x-runner-tag", "id:name")

        val count = 2 // 2 moves
        val output = ReplayProcessor.create<String>(count)

        val client = StandardWebSocketClient()
        client.execute(getUrl("/maze/move"), headers) { session ->
            session.send(moves.map { move -> session.textMessage(move) })
                    .thenMany(session.receive()
                            .take(count.toLong())
                            .map(WebSocketMessage::getPayloadAsText)
                            .subscribeWith(output)
                            .log())
                    .then()
        }.block(Duration.ofSeconds(5))

        val actual = output.collectList().block(Duration.ofSeconds(1))
        assertEquals(listOf(
                "(0,0)[(1,0)]",
                "(1,0)[(2,0),(0,0)]"), actual)
    }
}

