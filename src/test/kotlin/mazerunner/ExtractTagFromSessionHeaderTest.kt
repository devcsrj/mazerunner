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
