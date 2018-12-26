package mazerunner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Server

fun main(args: Array<String>) {
    SpringApplication.run(Server::class.java, *args)
}
