package paths.services.sample

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler

class SampleServiceImpl : SampleService {

    /*
      Award winning implementation of the 'reverse string' service
     */
    override fun reverse(text: String, resultHandler: Handler<AsyncResult<String>>) {
        resultHandler.handle(Future.succeededFuture(text.reversed()))
    }
}