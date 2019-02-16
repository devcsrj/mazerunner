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
