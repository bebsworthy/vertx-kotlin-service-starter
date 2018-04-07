package paths.services.auth;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.jetbrains.annotations.NotNull;

/**
 * The service interface.
 */
@ProxyGen // Generate the proxy and handler
@VertxGen // Generate clients in non-java languages
public interface AuthService {

    // Authenticate a user using a user name and password and return a JWT token
    void authenticate(
            @NotNull String login,
            @NotNull String password,
            @NotNull Handler<AsyncResult<String>> resultHandler);
}

