package paths.services.sample

import io.vertx.codegen.annotations.ProxyGen
import io.vertx.codegen.annotations.VertxGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

/**
 * The service interface.
 */
@ProxyGen // Generate the proxy and handler
@VertxGen // Generate clients in non-java languages
interface SampleService {

    // The service methods
    fun reverse(text: String, resultHandler: Handler<AsyncResult<String>>)
}
