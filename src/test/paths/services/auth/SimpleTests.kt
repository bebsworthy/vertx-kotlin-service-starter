package paths.services.auth

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue


/**
 * Very simple test class that demonstrate using JUnit 5
 */
class SimpleTests {
    /** data class used in this test **/
    data class Person(val firstName: String, val lastName: String)

    private lateinit var person: Person
    private lateinit var people: List<Person>

    companion object {
        @BeforeAll
        @JvmStatic
        fun initAll() {
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
        }
    }

    @BeforeEach
    fun init() {
        person = Person("John", "Doe")
        people = listOf(
                Person("John", "Doe"),
                Person("Jane", "Doe"),
                Person("Janine", "Doe")
        )
    }

    @Test
    fun succeedingTest() {
    }

    @Test
    fun failingTest(): Nothing {
        fail("a failing test")
    }

    @Test
    fun `grouped assertions`() {
        assertAll("person",
                { assertEquals("John", person.firstName) },
                { assertEquals("Doe", person.lastName) }
        )
    }

    @Test
    fun `exception testing`() {
        val exception = assertThrows<IllegalArgumentException>("Should throw an exception") {
            throw IllegalArgumentException("a message")
        }
        assertEquals("a message", exception.message)
    }

    @Test
    fun `assertions from a stream`() {
        assertAll(
                "people with name starting with J",
                people
                        .stream()
                        .map {
                            // This mapping returns Stream<() -> Unit>
                            { assertTrue(it.firstName.startsWith("J")) }
                        }
        )
    }

    @Test
    fun `assertions from a collection`() {
        assertAll(
                "people with last name of Doe",
                people.map { { assertEquals("Doe", it.lastName) } }
        )
    }

    @Test
    @Disabled("for demonstration purposes")
    fun skippedTest() {
        // not executed
    }

    @AfterEach
    fun tearDown() {
    }


}