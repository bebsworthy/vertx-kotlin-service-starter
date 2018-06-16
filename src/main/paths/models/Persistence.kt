package paths.models

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import javax.persistence.EntityTransaction


object PersistenceEnvironment {
    val registry: StandardServiceRegistry by lazy {
        StandardServiceRegistryBuilder()
                .configure()
                .build()
    }

    // A SessionFactory is set up once for an application!
    val sessionFactory: SessionFactory by lazy {
        MetadataSources(registry).buildMetadata().buildSessionFactory()
    }

    init {
        sessionFactory
    }

    fun shutdown() {
        StandardServiceRegistryBuilder.destroy(this.registry)
    }
}

val persistenceKodeinModule = Kodein.Module {

    bind<Session>() with provider { PersistenceEnvironment.sessionFactory.openSession() }

}

inline fun <R> Session.autoclose(block: (Session) -> R): R {
    try {
        return block(this)
    } finally {
        println("Closing session")
        this.close()
    }
}

inline fun <R> EntityTransaction.autocommit(block: (EntityTransaction) -> R): R {
    try {
        return block(this)
    } catch (e: Throwable) {
        if (this.isActive) this.rollback()
        throw e
    } finally {
        if (this.isActive) this.commit()
    }
}

inline fun <R> Session.autocommit(block: (Session) -> R): R {
    this.autoclose { session ->
        session.beginTransaction().autocommit {
            return block(this)
        }
    }
}
