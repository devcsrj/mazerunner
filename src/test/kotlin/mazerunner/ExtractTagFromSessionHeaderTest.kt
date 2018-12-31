package mazerunner

import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.socket.HandshakeInfo
import org.springframework.web.reactive.socket.WebSocketSession
import kotlin.test.assertEquals

class ExtractTagFromSessionHeaderTest {

    @Test
    fun `can extract from provided attribute name`() {
        val headers = HttpHeaders()
        headers["x-runner-tag"] = "id:name"

        val handshakeInfo = mock(HandshakeInfo::class.java)
        `when`(handshakeInfo.headers).thenReturn(headers)

        val session = mock(WebSocketSession::class.java)
        `when`(session.handshakeInfo).thenReturn(handshakeInfo)

        val fn = ExtractTagFromSessionHeader("x-runner-tag")
        val actual = fn.apply(session)
        assertEquals("id", actual!!.id)
        assertEquals("name", actual.name)
    }
}
