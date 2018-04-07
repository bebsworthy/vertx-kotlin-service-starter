package paths.services

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.serviceproxy.ServiceBinder

abstract class AbstractBusServiceVerticle : AbstractVerticle() {

    var consumer: MessageConsumer<JsonObject>? = null

    override fun stop(stopFuture: Future<Void>?) {

        if (consumer != null) {
            ServiceBinder(vertx).unregister(consumer)
            consumer = null
        }
    }
}
