package paths

import io.vertx.core.AbstractVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import paths.services.api.RestApiVerticle
import paths.services.auth.AuthVerticle
import paths.services.sample.SampleServiceBusConsumerVerticle
import paths.services.sample.SampleServiceBusVerticle

@Suppress("unused")
class MainVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)

    override fun start(startFuture: Future<Void>) {
        // Here we use the same deployment parameters for all the verticles
        // and we pass them all the main config()
        val serverOpts = DeploymentOptions()
                .setConfig(config())

        logger.info("MainVerticle xx:" + AuthVerticle::class.qualifiedName)

        // Deploy all the verticles we need
        CompositeFuture.all(
                deploy(AuthVerticle::class.qualifiedName ?: "", serverOpts),
                deploy(RestApiVerticle::class.qualifiedName ?: "", serverOpts),
                deploy(SampleServiceBusVerticle::class.qualifiedName ?: "", serverOpts),
                deploy(SampleServiceBusConsumerVerticle::class.qualifiedName ?: "", serverOpts)
                // deploy("io.vertx.ext.shell.ShellVerticle", serverOpts)

                // <-- Add verticle here

        ).setHandler {
            if (it.succeeded()) {
                startFuture.complete()
            } else {
                startFuture.fail(it.cause())
            }
        }
    }

    override fun stop(stopFuture: Future<Void>?) {
        val ids = vertx.deploymentIDs()

        for (s in ids) {
            logger.info(this.deploymentID())
        }
    }

    private fun undeploy(name: String, opts: DeploymentOptions): Future<Any> {
        val done = Future.future<Any>()

        vertx.deployVerticle(name, opts) {
            if (it.failed()) {
                System.out.println("Failed to deploy verticle $name")
                done.fail(it.cause())
            } else {
                System.out.println("Deployed verticle $name")
                done.complete()
            }
        }

        return done
    }

    private fun deploy(name: String, opts: DeploymentOptions): Future<Any> {
        val done = Future.future<Any>()

        vertx.deployVerticle(name, opts) {
            if (it.failed()) {
                System.out.println("Failed to deploy verticle $name")
                done.fail(it.cause())
            } else {
                System.out.println("Deployed verticle $name")
                done.complete()
            }
        }

        return done
    }
}
