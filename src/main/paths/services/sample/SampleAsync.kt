package paths.services.sample

import io.vertx.codegen.annotations.ProxyClose
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.serviceproxy.ServiceProxyBuilder
import kotlinx.coroutines.experimental.launch
import paths.services.AbstractBusServiceVerticle
import paths.services.auth.AuthService
import java.util.logging.Logger

/**
 * Simple interface for the service
 */
@ProxyGen
@VertxGen
interface SampleAsyncService {

    fun levelOne(data: String, resultHandler: Handler<AsyncResult<String>>)

    fun levelTwo(data: String, resultHandler: Handler<AsyncResult<JsonObject>>)

    @ProxyClose
    fun close()
}

/**
 * Implementation of the service
 */
class SampleAsyncServiceImpl(val vertx: Vertx) : SampleAsyncService {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }

    /**
     * LevelOne function
     */
    override fun levelOne(data: String, resultHandler: Handler<AsyncResult<String>>) {
        logger.info("Begin levelOne $data")

        levelTwo("$data.one", Handler { f ->

            if (f.succeeded()) {
                val r = f.result()
                logger.info("LevelOne: result from levelTwo: $r")
                // were done so we notify with a success
                resultHandler.handle(Future.succeededFuture("ok"))
            } else {
                logger.info("LevelOne: levelTwo Failed")
                // It failed so we return a failure and propagate the reason
                resultHandler.handle(Future.failedFuture(f.cause()))
            }
        })

        logger.info("End levelOne $data function")
    }

    /**
     * LevelOne function
     * Demonstrate how to call an asynchronous method using the coroutine style
     */
    override fun levelTwo(data: String, resultHandler: Handler<AsyncResult<JsonObject>>) {
        // Here we will call the auth service three time sequentially

        logger.info("Begin levelTwo")

        val builder = ServiceProxyBuilder(vertx).setAddress("auth-service")
        val service = builder.build(AuthService::class.java)

        val res = JsonObject()

        launch(vertx.dispatcher()) {

            var i = 3
            while (i > 0) {
                logger.info("levelTwo loop $i")
                i--
                try {
                    val reply = awaitResult<JsonObject> { f ->
                        service.authenticate("dora", "dora", f)
                    }
                    res.put("token$i", reply)
                    logger.info("result from authenticate $reply")
                } catch (e: Exception) {
                    logger.info("authenticate failed: $e")
                }
                logger.info("levelTwo end loop $i")
            }

            // Now we can complete the function here because it's in a coroutine block (async)
            resultHandler.handle(Future.succeededFuture(res))
        }

        logger.info("End levelTwo function")
    }

    @Override
    override fun close() {
        logger.info("the close() override")
    }
}

/**
 * Simple verticle to connect our service to the bus
 */
class SampleAsyncServiceVerticle : AbstractBusServiceVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "service-sample-async"
    }

    private val log = Logger.getLogger(this::class.qualifiedName)

    override fun start(startFuture: Future<Void>) {
        log.info("Starting SampleAsyncServiceVerticle...")

        val service = SampleAsyncServiceImpl(vertx)

        consumer = ServiceBinder(vertx).setAddress(SERVICE_ADDRESS)
                .register(SampleAsyncService::class.java, service)

        startFuture.complete()
    }
}
