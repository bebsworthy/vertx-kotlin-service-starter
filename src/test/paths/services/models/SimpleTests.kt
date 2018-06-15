package paths.services.models

import com.querydsl.jpa.impl.JPAQueryFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import paths.models.*



/**
 * Very simple test class that demonstrate using JUnit 5
 */
class SimpleTests {

    companion object {
        @BeforeAll
        @JvmStatic
        fun initAll() {

            /**
             * Setup the global injection module
             */
            Kodein.global.addImport(persistenceKodeinModule)

        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {

            /**
             * Shutdown the database
             */
            PersistenceEnvironment.shutdown()
        }
    }

    @Test
    fun `Try saving a few different way`() {

        val store = UserStore()

        /**
         * Save one in the database
         */
        val id = store.save(User("joe"))
        assertNotNull(id)

        val idNum = id.value<Long>()
        assertEquals(idNum, 1L)

        /**
         * Save a few in the database
         */
        val result = store.save(listOf(
                    User("jane"),
                    User("arthur") ))

        assertEquals(result.size, 2)
        assertEquals(result[0].value<Long>(), 2L)
        assertEquals(result[1].value<Long>(), 3L)

        /**
         * This is the preferred "type-safe" way to query the
         * database.
         */
        val users = store.queryFactory { factory, user ->
            factory.selectFrom(user)
                    .where(user.id.eq(1))
                    .fetch()
        }
        assertEquals(users?.size, 1)
        println("result = $users")

        /**
         * Query byId
         */
        store.byId(1)?.let {
            println("User ${it.id} ${it.name}")
        }

        // Query one that doesn't exist
        store.byId(55)?.let {
            println("User ${it.id} ${it.name}")
        }

        /**
         * Raw Hibernate SQL Query
         */
        store.rawQuery().forEach { user ->
            println("User ${user.id} ${user.name}")
        }

        /**
         * Session autocommit (Best version)
         */
        store.autocommit {
            // Here you get a Session object wrapped in a transaction
            //   whatever you do will be autocommited if there is no
            //   exception thrown (otherwise it's a rollback)
            it.save(User("Robert2"))
            it.save(User("Andrew2"))
        }

        /**
         * This is equivalent to the previous example
         */
        store.newSession().autocommit {
            // Here you get a Session object
            //  so you can do whatever you want with it
            it.save(User("Maxim"))
            it.save(User("Frederick"))
        }

        /**
         * Free Session
         * It's also possible to create a new session object
         *    (and to forget to close it)
         */

        val session = store.newSession()
        try {
            val transaction = session.transaction
            transaction.begin()

            session.save(User("Gerard"))
            session.save(User("Vanessa"))

            transaction.commit()
        } finally {
            session.close()
        }

        /**
         * If you want you can make your own entity manager
         */
        store.newSession().autoclose { session ->
            val entityManager = session.entityManagerFactory.createEntityManager()
            val query = entityManager.createQuery("from User where id=:id", User::class.java)
            query.setParameter("id", 1L)
            val result = query.resultList
            println("result = $result")
        }

    }



}