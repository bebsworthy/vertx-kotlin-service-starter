package paths.models


class UserStore : AbstractModelStore<User, QUser>() {

    override val queryBase: String = "from User"

    override val entity = QUser.user!!

    fun byId(id: Long): User? = rawSearchOne("id=:id", mapOf("id" to id))

}

