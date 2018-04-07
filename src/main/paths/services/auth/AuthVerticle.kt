package paths.services.auth

import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory

import io.vertx.serviceproxy.ServiceBinder
import paths.services.AbstractBusServiceVerticle

@Suppress("unused")
class AuthVerticle : AbstractBusServiceVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "auth-service"
    }

    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)
    private var service: AuthServiceImpl? = null

    override fun start(startFuture: Future<Void>) {
        logger.info("Starting " + this::class.qualifiedName)

        val options = AuthServiceImpl.AuthServiceOption(
                config().getString("auth.keystore"),
                config().getString("auth.keystore.password")
        )

        // The service implementation
        service = AuthServiceImpl(vertx, options)

        // Register the handler
        consumer = ServiceBinder(vertx)
                .setAddress(SERVICE_ADDRESS)
                .register(AuthService::class.java, service)

        startFuture.complete()
    }
}
