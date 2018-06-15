package paths.services.sample

import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import org.jetbrains.annotations.NotNull

/**
 * The service interface.
 */
@ProxyGen  // Generate the proxy and handler
@VertxGen  // Generate clients in non-java languages
interface SampleKService {
    fun reverse(@NotNull text: String, @NotNull resultHandler: Handler<AsyncResult<String>>)
}