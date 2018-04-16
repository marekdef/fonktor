import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.OutgoingContent
import io.ktor.features.CallLogging
import io.ktor.http.ContentType
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.experimental.delay
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        val random = Random()
        val base = File("images/")
        if(!base.exists()) {
            throw FileNotFoundException(base.absolutePath)
        }
        install(CallLogging)
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
            get("/font") {
                call.respondFile(base, "63.png", {
                    val contentType = ContentType.Image.PNG
                })
            }
            get("/font/{n?}") {
                val n = (call.parameters["n"] ?: " ").toCharArray().first()
                val delay = random.nextInt(5).toLong()
                delay(delay, TimeUnit.SECONDS)

                val contentTypeClosure : OutgoingContent.() -> Unit = {
                    val contentType = ContentType.Image.PNG
                }

                when(n) {
                    in '0'..'9' -> call.respondFile(base, "${n}.png", contentTypeClosure)
                    in 'A'..'Z' -> call.respondFile(base, "${n.dec()+36-'A'.dec()}.png", contentTypeClosure)
                    in 'a'..'z' -> call.respondFile(base, "${n.dec()+10-'a'.dec()}.png", contentTypeClosure)
                    '@' -> call.respondFile(base, "62.png", contentTypeClosure)
                    '.' -> call.respondFile(base, "64.png", contentTypeClosure)
                    else -> call.respondFile(base, "65.png", contentTypeClosure)
                }
            }
        }
    }
    server.start(wait = true)
}

private suspend fun randomDelay(random: Random) {
    delay(2 + random.nextInt(3).toLong(), TimeUnit.SECONDS)
}