package paths

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

/**
 * A verticle that does nothing!
 */
@Suppress("unused")
class NoopVerticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        startFuture.complete()
    }
}