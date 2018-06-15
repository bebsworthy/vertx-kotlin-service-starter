package paths.services.sample

import io.vertx.core.CompositeFuture
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.serviceproxy.ServiceProxyBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import paths.services.auth.AuthVerticle


/**
 * A simple JUnit5 test class for a vertx project
 */
@ExtendWith(VertxExtension::class)
class SampleAsyncServiceTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initAll() {
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
        }
    }

    private fun getService(vertx: Vertx): SampleAsyncService {
        val builder = ServiceProxyBuilder(vertx).setAddress(SampleAsyncServiceVerticle.SERVICE_ADDRESS)
        return builder.build(SampleAsyncService::class.java)
    }


    /** deploy a bunch of verticles */
    private fun deploy(vertx: Vertx, verticleName: String, options: DeploymentOptions): Future<Any> {
        val returnval = Future.future<Any>()

        vertx.deployVerticle(verticleName, options) { res ->
            if (res.succeeded()) {
                println("Deployed ok: $verticleName")
                returnval.complete()
            } else {
                println("Deploy failed! $verticleName")
                returnval.fail(res.cause())
            }

        }

        return returnval
    }

    /**
     * This is called before each test and deploys a fresh AuthenticationVerticle
     */
    @BeforeEach
    fun `Deploy Verticle`(vertx: Vertx, testContext: VertxTestContext) {

        val options = DeploymentOptions()
                .setConfig(
                        json {
                            obj(
                                    "auth.keystore" to "security-dev/keystore.pkcs12",
                                    "auth.keystore.password" to "secret"
                            )
                        }
                )

        CompositeFuture.all(
                deploy(vertx, AuthVerticle::class.qualifiedName ?: "", options),
                deploy(vertx, SampleAsyncServiceVerticle::class.qualifiedName ?: "", options)
        ).setHandler(testContext.succeeding {
            testContext.completeNow()
        })
    }


    @AfterEach
    fun tearDown() {
    }

    /**
     * test level one
     */
    @Test
    fun `Test levelOne`(vertx: Vertx, testContext: VertxTestContext) {
        val service = getService(vertx)
        val data = "dora"
        service.levelOne(data,
                testContext.succeeding {
                    assertEquals(it, "ok")
                    testContext.completeNow()
                })
    }
}