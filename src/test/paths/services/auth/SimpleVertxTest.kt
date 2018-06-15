package paths.services.auth

import io.vertx.core.CompositeFuture
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ServiceProxyBuilder
import kotlinx.coroutines.experimental.launch
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith


/**
 * A simple JUnit5 test class for a vertx project
 */
@ExtendWith(VertxExtension::class)
class SimpleVertxTest {

    private val serviceName = "auth-service"

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

        vertx.deployVerticle(AuthVerticle(), options,
                testContext.succeeding {
                    testContext.completeNow()
                })
    }

    /**
     * No need to do anything because vertx automatically handle undeploying the verticles
     * that we created in @BeforeEach
     */
    @AfterEach
    fun tearDown() {
    }

    /**
     * Most basic example of test case using vertx
     */
    @Test
    fun `An http test`(vertx: Vertx, testContext: VertxTestContext) {

        vertx.createHttpServer()
                .requestHandler { req -> req.response().end() }
                .listen(16969, testContext.succeeding { _ -> testContext.completeNow() })

    }

    /**
     * Authentication attempt for the user Dora
     */
    @Test
    fun `Authenticate Dora`(vertx: Vertx, testContext: VertxTestContext) {
        val builder = ServiceProxyBuilder(vertx).setAddress(serviceName)
        val authService = builder.build(AuthService::class.java)

        authService.authenticate("dora", "dora",
                testContext.succeeding {
                    assertNotNull(it)
                    testContext.completeNow()
                })
    }

    /**
     * This function launch an authentication for many users and uses
     * a list of Future to synchronize them and detect the end of the test
     */
    @Test
    fun `Authenticate many users`(vertx: Vertx, testContext: VertxTestContext) {
        val builder = ServiceProxyBuilder(vertx).setAddress(serviceName)
        val authService = builder.build(AuthService::class.java)


        val users = listOf(
                Pair("dora", "dora"),
                Pair("user", "user")
        )

        val list = ArrayList<Future<String>>()

        println("before users.stream()")
        users.map { user ->
            val f = Future.future<String>()
            list.add(f)
            println("Launching an new authentication for ${user.first}")
            authService.authenticate(user.first, user.second,
                    testContext.succeeding {
                        assertNotNull(it)
                        println("Authentication complete for ${user.first}")
                        f.complete()
                    })
        }

        launch(vertx.dispatcher()) {
            println("Waiting for ${list.size} futures")
            CompositeFuture.all(list.toList()).await()
            println("Done waiting, test complete")
            testContext.completeNow()
        }
    }

    /**
     * This function shows how to launch a verticle and perform a test.
     * It is disabled because the creation of the verticle for each function has been
     * moved to @BeforeEach
     */
    @Disabled
    @Test
    fun `Launch a verticle and test`(vertx: Vertx, testContext: VertxTestContext) {

        val options = DeploymentOptions()
                .setConfig(
                        json {
                            obj(
                                    "auth.keystore" to "temp/keystore.pkcs12",
                                    "auth.keystore.password" to "admin12345"
                            )
                        }
                )

        val builder = ServiceProxyBuilder(vertx).setAddress(serviceName)
        val authService = builder.build(AuthService::class.java)

        vertx.deployVerticle(AuthVerticle(), options,
                testContext.succeeding {
                    authService.authenticate("dora", "dora1234",
                            testContext.succeeding {
                                assertNotNull(it)
                                testContext.completeNow()
                                // testContext.failNow(Exception())
                            })
                })
    }


}