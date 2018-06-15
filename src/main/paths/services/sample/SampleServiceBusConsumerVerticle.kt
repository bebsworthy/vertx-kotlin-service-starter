package paths.services.sample

import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ServiceProxyBuilder
import kotlinx.coroutines.experimental.launch
import paths.services.AbstractHttpServiceVerticle

/**
 * A verticle that consume a service on the bus
 */
@Suppress("unused")
class SampleServiceBusConsumerVerticle : AbstractHttpServiceVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "sample-bus-service-address"
    }

    override suspend fun start() {
        // Set up the router
        val router = Router.router(vertx)

        // Create a router that process the request using the service on the bus
        router.get("/:message").handler { ctx ->

            // Obtain the handler to the service
            val builder = ServiceProxyBuilder(vertx).setAddress(SERVICE_ADDRESS)
            val service = builder.build(SampleService::class.java)

            // Get the message from the request
            val message = ctx.request().getParam("message") ?: ""

            launch(vertx.dispatcher()) {
                try {
                    val result = awaitResult<String> { f ->
                        service.reverse(message, f)
                    }

                    // Success, return whatever we received
                    ctx.response().putHeader("Content-Type", "text/plain")
                    ctx.response().end(result)
                } catch (e: Exception) {

                    // Return the world DELIAF
                    //   -> By design we don't differentiate between a successful processing
                    //      of the word FAILED and an error message
                    // I don't recommend following this pattern if you design braking system for cars but it should
                    // be ok for an emission management system at VW.
                    ctx.response().putHeader("Content-Type", "text/plain")
                    ctx.response().end("DELIAF")
                }
            }
        }

        // Start the server
        startServer(9090, router)
    }
}