package nest.validation

class User {

    String username

    static hasOne = [
            channel: Channel
    ]

	static belongsTo = [organization: Organization]

}
