package paths.models


import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table( name = "USERS" )
data class User(
        @Column(nullable = false)
        val name: String,

        @Id
        @GeneratedValue(generator="increment")
        @GenericGenerator(name="increment", strategy = "increment")
        val id: Long = 0
) {

    @Column(nullable = false)
    val password: String = ""
}

//data class Metadata(
//        val creation_date: LocalDateTime,
//        val created_by: UserSummary,
//        val modification_date: LocalDateTime,
//        val modified_by: UserSummary,
//        val owner: UserSummary
//)
