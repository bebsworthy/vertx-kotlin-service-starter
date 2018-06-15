package paths.models

import java.time.LocalDateTime

class FlowController {

    private val mockMetadata by lazy {
        Metadata(
                LocalDateTime.now(),
                UserSummary(1, "Joe"),
                LocalDateTime.now(),
                UserSummary(1, "Joe"),
                UserSummary(1, "Joe")
        )
    }

    private val mockFlow by lazy {
        listOf(
                Flow(mockMetadata, 1, "Learn Python in 12 days", "video"),
                Flow(mockMetadata, 2, "Kotlin from 0 to 100", "blog")
        )
    }
}