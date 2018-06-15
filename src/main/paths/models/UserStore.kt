package paths.models

import com.querydsl.jpa.impl.JPAQueryFactory
import org.hibernate.Session
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import org.kodein.di.generic.provider



class UserStore : AbstractModelStore<User, QUser>() {

    override val queryBase: String = "from User"

    override val entity = QUser.user!!

    fun byId(id: Long): User? = rawQueryOne("id=:id", mapOf("id" to id))

}

