import org.jetbrains.exposed.dao.id.IntIdTable

data class User(
    val id : Int = -1,
    val lastname: String,
    val firstname: String,
    val middlename: String,
    val age: Int,
    val gender: Gender,
) {
    enum class Gender(val code: Byte) {
        MALE(0), FEMALE(1)
    }
}

object UserTable : IntIdTable() {
    val lastname = varchar("lastname", 100)
    val firstname = varchar("firstname", 100)
    val middlename = varchar("middlename", 100)
    val age = integer("age")
    val gender = byte("gender")
}