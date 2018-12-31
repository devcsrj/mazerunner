package mazerunner

import org.springframework.web.reactive.socket.WebSocketSession
import java.util.function.Function

/**
 * Takes the attribute whose name matches [attrName].
 *
 * This expects a value in the form of [id:name], and constructs a [Tag] from it
 */
class ExtractTagFromSessionHeader(private val attrName: String) : Function<WebSocketSession, Tag?> {

    override fun apply(t: WebSocketSession): Tag? {
        val value = t.handshakeInfo.headers.getFirst(attrName) ?: return null
        if (!value.contains(":"))
            return null
        val tokens = value.split(":")
        return Tag(tokens[0], tokens[1])
    }
}
