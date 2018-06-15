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
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.serviceproxy.ServiceProxyBuilder
import kotlinx.coroutines.experimental.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


/**
 * Demonstrate how to scale a service using vertx
 */
@ExtendWith(VertxExtension::class)
class ScalingVertxTest {

    private val serviceName = "auth-service"

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

        // Starts a lot of verticles
        options.instances = 10

        val classPath = AuthVerticle::class.qualifiedName

        println("Launching ${options.instances} verticles")
        launch(vertx.dispatcher()) {
            awaitResult<String> {
                vertx.deployVerticle(classPath, options, it)
            }
            println("Verticles launched")
            testContext.completeNow()
        }
    }

    /**
     * No need to do anything because vertx automatically handle undeploying the verticles
     * that we created in @BeforeEach
     */
    @AfterEach
    fun tearDown() {
    }


    /**
     * This function launch an authentication for many users and uses
     * a list of Future to synchronize them and detect the end of the test
     */
    @Test
    fun `Authenticate many users`(vertx: Vertx, testContext: VertxTestContext) {
        val builder = ServiceProxyBuilder(vertx).setAddress(serviceName)
        val authService = builder.build(AuthService::class.java)

        // Generate 100 dummy user
        var count = 10000
        val users = generateSequence {
            if (count-- > 0)
                Pair("dora", "dora")
            else // and that will terminate the sequence
                null
        }

        val list = ArrayList<Future<String>>()

        println("Launching queries")
        users.forEach { user ->
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

}