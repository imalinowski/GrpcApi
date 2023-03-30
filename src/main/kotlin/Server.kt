import Register.Result
import Register.User
import Register.User.Gender.FEMALE
import Register.User.Gender.MALE
import RegistrationServiceGrpcKt.RegistrationServiceCoroutineImplBase
import io.grpc.ServerBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Integer.min
import User as UserDao

private class RegistrationService : RegistrationServiceCoroutineImplBase() {

    private val daoImpl = DAOFacadeImpl()

    override suspend fun put(request: User): Result =
        with(request) {
            val id = daoImpl.addNewUser(
                lastname, firstname, middlename, age,
                gender.toByte()
            )
            println("Registering user ${request.lastname}")
            return result {
                succeeded = id != -1
                if (id != -1) {
                    message = "Id is $id"
                } else {
                    error = "User Create Error"
                }
            }
        }

    override fun getMany(request: Register.ManyRequest): Flow<Register.ManyUsers> =
        flow {
            val allUsers = daoImpl.allUsers().map {
                user {
                    lastname = it.lastname
                    firstname = it.firstname
                    middlename = it.middlename
                    age = it.age
                    gender = it.gender.map()
                }
            }
            println("get many : send ${allUsers.size} users")
            for (i in 1..request.pageNum) {
                val from = (i - 1) * request.pageLength
                val to = i * request.pageLength
                emit(
                    manyUsers {
                        users.addAll(
                            allUsers.subList(from, min(to, allUsers.size))
                        )
                    }
                )
            }
        }.catch {
            emit(
                manyUsers { error = it.message ?: "unknown error" }
            )
        }


    override suspend fun get(request: Register.Id): User =
        try {
            val user = daoImpl.getUser(request.id)
            println("Send User with id ${request.id} is ${user != null}")
            user {
                lastname = user?.lastname ?: ""
                firstname = user?.firstname ?: ""
                middlename = user?.middlename ?: ""
                age = user?.age ?: 0
                gender = user?.gender.map()
            }
        } catch (t: Throwable) {
            user {
                lastname = ""
            }
        }

    override suspend fun delete(request: Register.Id): Result {
        val success = daoImpl.deleteUser(request.id)
        println("Delete User with id ${request.id} is $success")
        return result {
            succeeded = success
        }
    }

    private fun UserDao.Gender?.map(): User.Gender =
        when (this) {
            UserDao.Gender.MALE -> MALE
            else -> FEMALE
        }

    private fun User.Gender.toByte() =
        when (this) {
            MALE -> 0.toByte()
            else -> 1.toByte()
        }
}

fun main() {
    DatabaseFactory.init()

    val port = PORT
    //prepare and run the gRPC web server
    val server = ServerBuilder
        .forPort(port)
        .addService(RegistrationService())
        .build()
    server.start()
    //shutdown on application terminate
    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown()
    })
    //wait for connection until shutdown
    server.awaitTermination()
}