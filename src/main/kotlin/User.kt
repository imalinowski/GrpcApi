import org.jetbrains.exposed.dao.id.IntIdTable

data class User(
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val age: Int,
    val gender: Gender,
) {
    enum class Gender(code: Int) {
        MALE(0), FEMALE(1)
    }
}

object UserTable : IntIdTable() {
    val lastname = varchar("lastname", 100)
    val firstname = varchar("firstname", 100)
    val middlename = varchar("middlename", 100)
    val age = integer("age")
    val gender = binary("gender")
}