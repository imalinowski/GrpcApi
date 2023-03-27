import DatabaseFactory.dbQuery
import User.Gender.FEMALE
import User.Gender.MALE
import UserTable.lastname
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


interface DAOFacade {
    suspend fun getUser(id: Int): User?
    suspend fun addNewUser(
        lastname: String,
        firstname: String,
        middlename: String,
        age: Int,
        gender: Byte
    ): Boolean
    suspend fun deleteArticle(id: Int): Boolean
    suspend fun allUsers(): List<User>
}

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToArticle(row: ResultRow) = User(
        id = row[UserTable.id].value,
        lastname = row[lastname],
        firstname = row[UserTable.firstname],
        middlename = row[UserTable.middlename],
        age = row[UserTable.age],
        gender = if (row[UserTable.gender] == 0.toByte()) FEMALE else MALE,
    )

    override suspend fun getUser(id: Int): User? = dbQuery {
        UserTable
            .select { UserTable.id eq id }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addNewUser(
        lastname: String,
        firstname: String,
        middlename: String,
        age: Int,
        gender: Byte
    ): Boolean = dbQuery {
        val insertStatement = UserTable.insert {
            it[UserTable.lastname] = lastname
            it[UserTable.firstname] = firstname
            it[UserTable.middlename] = middlename
            it[UserTable.age] = age
            it[UserTable.gender] = gender
        }
        insertStatement.insertedCount > 0
    }

    override suspend fun deleteArticle(id: Int): Boolean = dbQuery {
        UserTable.deleteWhere { UserTable.id eq id } > 0
    }

    override suspend fun allUsers(): List<User> = dbQuery {
        UserTable.selectAll().map(::resultRowToArticle)
    }
}