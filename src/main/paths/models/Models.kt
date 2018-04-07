package paths.models

import java.time.LocalDateTime

data class User(
        val id: Int,
        val name: String,
        val password: String
)

data class UserSummary(
        val id: Int,
        val name: String
)

data class Metadata(
        val creation_date: LocalDateTime,
        val created_by: UserSummary,
        val modification_date: LocalDateTime,
        val modified_by: UserSummary,
        val owner: UserSummary
)

data class Flow(
        val metadata: Metadata,
        val id: Int,
        val title: String,
        val media: String
)