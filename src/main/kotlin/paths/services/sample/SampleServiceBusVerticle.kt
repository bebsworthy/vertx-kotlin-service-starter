package paths.services.sample

import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.serviceproxy.ServiceBinder

/**
 * A verticle that register a service on the bus
 */
@Suppress("unused")
class SampleServiceBusVerticle : CoroutineVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "sample-bus-service-address"
    }

    val service by lazy {
        SampleServiceImpl()
    }

    var consumer: MessageConsumer<JsonObject>? = null

    override suspend fun start() {

        // Register the handler
        consumer = ServiceBinder(vertx)
                .setAddress(SERVICE_ADDRESS)
                .register(SampleService::class.java, service)
    }

    override suspend fun stop() {
        if (consumer != null)
            ServiceBinder(vertx).unregister(consumer)
    }
}