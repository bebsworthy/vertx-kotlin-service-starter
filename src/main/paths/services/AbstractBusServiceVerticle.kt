package paths.services

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject

abstract class AbstractBusServiceVerticle : AbstractVerticle() {

    var consumer: MessageConsumer<JsonObject>? = null

    override fun stop(stopFuture: Future<Void>?) {

        val c = consumer
        if (c != null) {
            c.unregister()
            consumer = null
        }
        stopFuture?.complete()
    }
}
