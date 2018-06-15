package paths.services.auth

import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject

/**
 * The service interface.
 */
@ProxyGen
@VertxGen
interface AuthService {

    // Authenticate a user using a user name and password and return a JWT token
    fun authenticate(username: String,
                     password: String,
                     resultHandler: Handler<AsyncResult<JsonObject>>)

}

