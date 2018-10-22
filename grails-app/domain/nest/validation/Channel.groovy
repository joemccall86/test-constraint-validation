package nest.validation

class Channel {
    static belongsTo = [organization: Organization, owner: User]
}
