package paths.services.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.auth.KeyStoreOptions
import io.vertx.kotlin.ext.jwt.JWTOptions
import org.jetbrains.annotations.NotNull

class AuthServiceImpl(vertx: Vertx, options: AuthServiceOption) : AuthService {
    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)
    private val jwt: JWTAuth
    private val jwtOptions = JWTOptions(
            expiresInMinutes = 60
    )

    data class AuthServiceOption(val keystore: String,
                                 val password: String)

    init {
        logger.info("Loading keystore at " + options.keystore)

        // Set up the JWT options
        val jwtAuthOptions = JWTAuthOptions()
        jwtAuthOptions.keyStore = KeyStoreOptions(
                type = "pkcs12",
                path = options.keystore,
                password = options.password)

        // Create the JWT token builder
        jwt = JWTAuth.create(vertx, jwtAuthOptions)
    }

    override fun authenticate(@NotNull username: String, @NotNull password: String, @NotNull resultHandler: Handler<AsyncResult<String>>) {

        logger.info("Authenticate request for '$username'")

        // Dummy validation method
        if (password != username) {
            // Login failed... woohoo
            resultHandler.handle(Future.failedFuture("Invalid credentials"))
            logger.info("Authentication failure for '$username'")
            return
        }

        // User has been validated: Generate a token containing the username
        val token = jwt.generateToken(json { obj("username" to username) }, jwtOptions)
        resultHandler.handle(Future.succeededFuture(token))
    }
}
