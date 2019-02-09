package mazerunner

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "maze")
open class MazeProperties {

    var columns: Int = 9
    var rows: Int = 9
}


@Configuration
@ConfigurationProperties(prefix = "runner")
open class RunnerProperties {

    var lifespan: Long = 10 * 1000 // 10 seconds
}
