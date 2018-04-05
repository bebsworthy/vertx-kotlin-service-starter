package paths.services.sample;

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
public interface SampleService {

    // The service methods
    void reverse(@NotNull String text, @NotNull Handler<AsyncResult<String>> resultHandler);
}
