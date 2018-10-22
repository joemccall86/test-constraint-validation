import grails.testing.gorm.DataTest
import nest.validation.Channel
import nest.validation.Organization
import nest.validation.User
import spock.lang.Specification
import spock.lang.Unroll

class TestConstraintSpec extends Specification implements DataTest {

    def setupSpec() {
        mockDomain Organization
        mockDomain User
        mockDomain Channel
    }

    @Unroll
    def '#num users do not generate #errorCount errors when only 1 user is invalid'() {
        given:
        def organization = new Organization().save(flush: true)

        and:
        // The number of errors increases exponentially with this, using the formula 2^(n-1)
        // This makes our application run out of memory and become unresponsive, since we can have hundreds of users.
        num.times { i ->
            new User(
                    organization: organization,
                    username: "user${i}@example.com",
                    channel: new Channel(organization: organization)
            ).save(flush: true)
        }

        organization.refresh()

        when:
        organization.users.first().username = null

        then:
        !organization.validate()
        organization.errors.errorCount != 8
        organization.errors.errorCount == 1

        where:
        num | errorCount
        4   | 8
    }
}
