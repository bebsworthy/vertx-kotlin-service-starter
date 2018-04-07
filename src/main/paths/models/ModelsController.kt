package paths.models

import java.time.LocalDateTime

class FlowController {

    fun getFlows(tags: List<String>, limit: Int): List<Flow> {
        return MOCK_FLOWS
    }

    private val MOCK_METADATA by lazy {
        Metadata(
                LocalDateTime.now(),
                UserSummary(1, "Joe"),
                LocalDateTime.now(),
                UserSummary(1, "Joe"),
                UserSummary(1, "Joe")
        )
    }

    private val MOCK_FLOWS by lazy {
        listOf(
                Flow(MOCK_METADATA, 1, "Learn Python in 12 days", "video"),
                Flow(MOCK_METADATA, 2, "Kotlin from 0 to 100", "blog")
        )
    }
}