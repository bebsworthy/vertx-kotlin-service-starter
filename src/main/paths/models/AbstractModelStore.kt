package paths.models

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.hibernate.Session
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import org.kodein.di.generic.provider
import kotlin.reflect.KClass


abstract class AbstractModelStore<T, QT> {

    data class Id(val id: java.io.Serializable?) {
        inline fun <reified T> value(): T? {
            id?.let { if (id is T) return id }
            return null
        }
    }
    /**
     * Session Factory provided by the Kodein Global Object
     */
    val newSession : () -> Session by Kodein.global.provider()

    /**
     * Query base "from xxx" based on the name of the entity
     */
    abstract val queryBase: String
    abstract val entity: QT

    /**
     * Autocommit helper
     */
    inline fun <Z> autocommit(block: (session: Session) -> Z): Z {
        newSession().autocommit {
            return block(it)
        }
    }

    /**
     * Execute a raw JPA query of the form:
     * ```
     *  from EntityName where $whereClause
     * ```
     * There is no validation and the query is assumed to return a list of 'T'
     */
    fun rawQuery(whereClause: String? = null, parameters: Map<String, Any?>? = null): List<T> {
        // Query
        var queryStr = queryBase
        whereClause?.let {
            queryStr = "$queryStr where $whereClause"
        }

        return autocommit {

            val query = it.createQuery(queryStr)
            parameters?.let {
                it.forEach { param ->
                    query.setParameter(param.key, param.value)
                }
            }
            query.list() as List<T>
        }
    }

    /**
     * Calls `rawQuery` and return the first result of null
     */
    fun rawQueryOne(whereClause: String? = null, parameters: Map<String, Any?>? = null) : T? {
        val r = rawQuery(whereClause, parameters)
        return if (r.isNotEmpty()) r[0] else null
    }

    fun queryFactory(block: (query: JPAQueryFactory, entity: QT) -> List<T>?): List<T>? {

        // Query one using DSL
        println("- Query one with DSL")
        newSession().autoclose { session ->
            val entityManager = session.entityManagerFactory.createEntityManager()
            val queryFactory = JPAQueryFactory(entityManager)
            return block(queryFactory, entity)
        }

    }

    /**
     * Save one object
     */
    fun save(user: T): Id {
        val u = newSession().autocommit {
            Id(it.save(user))
        }
        println("Saved: $u")
        return u
    }

    /**
     * Save a list of object
     */
    fun save(users: List<T>) : List<Id> {
        val result = mutableListOf<Id>()
        newSession().autocommit { session ->
            users.forEach { result.add(Id(session.save(it))) }
        }
        return result
    }
}