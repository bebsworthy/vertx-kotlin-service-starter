package paths.services.api

import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ServiceProxyBuilder
import kotlinx.coroutines.experimental.launch
import paths.services.AbstractHttpServiceVerticle
import paths.services.auth.AuthService
import paths.services.auth.AuthVerticle

@Suppress("unused")
class RestApiVerticle : AbstractHttpServiceVerticle() {
    companion object {
        const val SERVICE_NAME = "API-service"
        const val CONFIG_PORT_KEY = "api.http.port"
        const val CONFIG_PORT_DEFAULT = 8080
        val logger = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }


    /**
     * CORS: List of allowed header
     */
    private val headers = setOf("x-requested-with",
            "Access-Control-Allow-Origin",
            "origin",
            "Content-Type",
            "accept",
            "Authorization")

    /**
     * CORS: List of allowed HTTP methods
     */
    private val methods = setOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.HEAD, HttpMethod.PATCH, HttpMethod.OPTIONS, HttpMethod.DELETE, HttpMethod.TRACE)


    /**
     * CORS: The CORS handler instance
     */
    private val corsHandler = CorsHandler.create("*")
            .allowedHeaders(headers)
            .allowedMethods(methods)
            //.allowCredentials(true)
            .maxAgeSeconds(8600)!!

    override suspend fun start() {
        logger.info("Starting " + this::class.qualifiedName)

        // Using coroutine 'awaitResult'
        val factory = awaitResult<OpenAPI3RouterFactory> { f -> OpenAPI3RouterFactory.create(vertx, "src/main/resources/api.yaml", f) }

        // Install the handlers
        factory.addHandlerByOperationId("authenticate-password", Handler<RoutingContext>(this::authenticate))
        factory.addHandlerByOperationId("getFlows", Handler<RoutingContext>(this::getFlows))

        // What's our server port?
        val port = config.getInteger(CONFIG_PORT_KEY, CONFIG_PORT_DEFAULT)


        // Get the router
        val router = factory.router

        // Add the CORS Handler
        addCorsHandler(router)

        // Do I even understand that?
        launch(vertx.dispatcher()) {
            startServer(port, router)

            // This happens after the startServer completes
            logger.info("Up and running")
        }
    }

    private fun addCorsHandler(router: Router) {
        val route = router.route()!!
        // Just to be sure the route execute before any other
        route.order(-1000)
        route.handler(corsHandler)
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
        service.authenticate(username, password, Handler {
            when {
                it.succeeded() -> {
                    logger.info("Successful authentication for '$username'")
                    context.response()
                            .setStatusCode(200)
                            .endWithJson(it.result())
                }
                else -> {
                    logger.error("Authentication failure for '$username'", it.cause())

                    context.response()
                            .setStatusCode(401)
                            .end("Authentication failed")
                }
            }
        })
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
}