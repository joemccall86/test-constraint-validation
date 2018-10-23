import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import nest.validation.Channel
import nest.validation.Organization
import nest.validation.User
import spock.lang.Specification

@Integration
@Rollback
class TestConstraintIntegrationSpec extends Specification {

    def '4 users do not generate 8 errors when only 1 user is invalid'() {
        given:
        def organization = new Organization().save(flush: true)

        and:
        // The number of errors increases exponentially with this, using the formula 2^(n-1)
        // This makes our application run out of memory and become unresponsive,
        // since even up to 100 users can generate an obscene number of errors
        // and lead to a stack overflow.
        4.times { i ->
            def user = new User(
                    organization: organization,
                    username: "user${i}@example.com",
                    channel: new Channel(organization: organization)
            )
            user.channel = new Channel(organization: organization, owner: user)
            organization.addToUsers(user)
        }

        organization.save(flush: true, failOnError: true)

        when: 'a single user is invalid'
        organization.users.first().username = null

        then:
        !organization.validate()
        organization.errors.errorCount != 8
        organization.errors.errorCount == 1
        organization.errors.getFieldError('users[0].username').code == 'nullable'
    }
}
