import grails.test.mixin.integration.Integration
import grails.transaction.Rollback


import nest.validation.Organization
import nest.validation.Channel
import nest.validation.User
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class TestConstraintIntegrationSpec extends Specification {

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
        organization.errors.errorCount != errorCount

        where:
        num | errorCount
        4   | 8
    }
}
