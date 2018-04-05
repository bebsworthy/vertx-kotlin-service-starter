package paths.services

import io.vertx.core.Future
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import kotlinx.coroutines.experimental.async
import paths.models.FlowController

@Suppress("unused")
abstract class AbstractHttpServiceVerticle : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger("AbstractHttpServiceVerticle.FlowVerticle")
    private val flowsController = FlowController()

    val discovery: ServiceDiscovery by lazy {
        ServiceDiscovery.create(vertx)
    }
    private var record: Record? = null
    private var server: HttpServer? = null

    suspend fun startServer(port: Int, router: Router) {

        server = awaitResult<HttpServer> { f ->
            vertx.createHttpServer()
                    .requestHandler { router.accept(it) }
                    .listen(port, f)
        }

        // if the server cannot start we should get an exception

        logger.info("HTTP Server start success")
    }

    /**
     * Automatically unpublish the service when it stops
     */
    override suspend fun stop() {
        logger.info("Stopping " + this::class.qualifiedName)

        // Shutting the server AND waiting for the answer should prevents a
        //  java.net.BindException: Address already in use with gradle (but does not)
        async {
            unpublishRecord()
            shutdownServer()
        }
    }

    /**
     * Publish the service record
     */
    suspend fun publishServiceRecord(name: String, host: String, port: Int, root: String) {
        logger.info("Publishing service:")
        val record = HttpEndpoint.createRecord(name, host, port, root)
        this.record = awaitResult<Record> { f -> discovery.publish(record, f) }
        logger.info("Service published")
    }

    /**
     * Unpublish the service record
     */
    private suspend fun unpublishRecord() {
        logger.info("Unpublishing service")

        val record = this.record

        // Never published (or publication failed)
        if (record != null) {
            // Unpublish and nullify the record
            awaitResult<Void> { f -> discovery.unpublish(record.registration, f) }
            discovery.close()
            this.record = null
        }
    }

    private suspend fun shutdownServer() {
        if (server != null) {
            awaitResult<Void> { f -> server?.close(f) }
            server = null
        }
    }

    //
    // Utilities

    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(data: Any) {

        this.putHeader("Content-Type", "application/json; charset=utf-8")

        if (data is JsonObject) {
            this.end(data.encodePrettily())
        } else {
            this.end(io.vertx.core.json.Json.encodePrettily(data))
        }
    }

    /**
     * Get a service
     */
    fun getServiceClientByName(name: String): Future<HttpClient> {
        val future = Future.future<HttpClient>()

        // TODO: rewrite with coroutine

        // Get a record by name
        discovery.getRecord({ r ->
            r.name == name
        }, { ar ->
            if (ar.succeeded()) {
                val record = ar.result()
                if (record != null) {
                    // we have a record, let's get a reference and a client
                    val reference = discovery.getReference(record)
                    val client = reference.getAs(HttpClient::class.java)
                    future.complete(client)
                } else {
                    future.fail("The lookup succeeded, but no matching service")
                }
            } else {
                future.fail(ar.cause())
                // lookup failed
            }
        })
        return future
    }
}
