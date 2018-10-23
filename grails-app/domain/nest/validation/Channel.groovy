package nest.validation

class Channel {

    User owner

    // workaround: manually handle cascading deletions in a service method
//    static belongsTo = [organization: Organization, owner: User]

    static belongsTo = [organization: Organization, owner: User]
}
