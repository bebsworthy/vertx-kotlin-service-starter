package paths.services.api

import io.vertx.core.Handler
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.serviceproxy.ServiceProxyBuilder
import kotlinx.coroutines.experimental.async
import paths.services.AbstractHttpServiceVerticle
import paths.services.auth.AuthService
import paths.services.auth.AuthVerticle

@Suppress("unused")
class RestApiVerticle : AbstractHttpServiceVerticle() {
    companion object {
        const val SERVICE_NAME = "API-service"
        const val CONFIG_PORT_KEY = "api.http.port"
        const val CONFIG_PORT_DEFAULT = 8080
    }

    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)

    override suspend fun start() {
        logger.info("Starting " + this::class.qualifiedName)

        // Using coroutine 'awaitResult'
        val factory = awaitResult<OpenAPI3RouterFactory> { f -> OpenAPI3RouterFactory.create(vertx, "src/main/resources/api.yaml", f) }

        // Install the handlers
        factory.addHandlerByOperationId("authenticate-password", Handler<RoutingContext>(this::authenticate))
        factory.addHandlerByOperationId("getFlows", Handler<RoutingContext>(this::getFlows))

        // What's our server port?
        val port = config.getInteger(CONFIG_PORT_KEY, CONFIG_PORT_DEFAULT)

        // Do I even understand that?
        async {
            startServer(port, factory.router)

            // This happens after the startServer completes
            logger.info("Up and running")
        }
    }

    private fun authenticate(context: RoutingContext) {
        logger.info("Authenticating")

        // Retrieve the parameters defined in the API
        val params: RequestParameters = context.get<Any>("parsedParameters") as RequestParameters

        // retrieve specific parameters
        val username = params.queryParameter("username")?.string ?: ""
        val password = params.queryParameter("password")?.string ?: ""

        // Obtain the handler to the service
        val builder = ServiceProxyBuilder(vertx).setAddress(AuthVerticle.SERVICE_ADDRESS)
        val service = builder.build(AuthService::class.java)

        // TODO: Handle error, missing service, timeout, etc..
        // For the time being we assume that the service will always exists and return something
        //  (Because I haven't figured that part out yet)

        logger.info("Calling authentication service for '$username'")
        service.authenticate(username, password) {
            when {
                it.succeeded() -> {
                    logger.info("Successful authentication for '$username'")
                    context.response()
                            .setStatusCode(200)
                            .endWithJson(json { obj("token" to it.result()) })
                }
                else -> {
                    logger.error("Authentication failure for '$username'", it.cause())

                    context.response()
                            .setStatusCode(401)
                            .end("Authentication failed")
                }
            }
        }
    }

    /**
     *
     * E.g.:
     *  http://localhost:8080/flows?tags=a,b,c
     */
    private fun getFlows(context: RoutingContext) {

        context.response()
                .setStatusCode(501)
                .end("Not implemented")
    }

//    private fun getTest(context: RoutingContext) {
//
//        // Example of how to get a service and call it from the registry
//        // (Not needed if we use BUS service)
//        getServiceClientByName("auth-service").setHandler {
//            when {
//                it.succeeded() -> {
//                    val client = it.result()
//                    client.getNow("/auth") { response ->
//                        response.bodyHandler { body ->
//                            val token = body.toString()
//                            // Don't forget to release the service
//                            ServiceDiscovery.releaseServiceObject(discovery, client)
//                            // End the response
//                            context.response().endWithJson(json { obj("token" to token) })
//                        }
//                    }
//                }
//                else -> {
//                    context.response().endWithJson(json { obj("token" to "failed") })
//                }
//            }
//        }
//    }
}
