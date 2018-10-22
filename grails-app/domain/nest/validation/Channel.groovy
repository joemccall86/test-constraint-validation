package nest.validation

class Channel {

    User owner

    static belongsTo = [organization: Organization, owner: User]

    static constraints = {
        owner nullable: true
    }
}
