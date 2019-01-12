package mazerunner

import mazerunner.MazeMovementWebSocketHandler.Companion.extractPoint
import org.junit.Test
import kotlin.test.assertEquals

class MazeMovementWebSocketHandlerTest {

    @Test
    fun `can transform coordinates to Points`() {
        var actual = extractPoint("(0,0)")
        assertEquals(actual, Point(0, 0))

        actual = extractPoint("(0,1)")
        assertEquals(actual, Point(0, 1))

        actual = extractPoint("(1,1)")
        assertEquals(actual, Point(1, 1))

        actual = extractPoint("(-1,-1)")
        assertEquals(actual, Point(-1, -1))
    }

}
