import Register.Result
import Register.User
import RegistrationServiceGrpcKt.RegistrationServiceCoroutineImplBase
import io.grpc.ServerBuilder
import User as UserDao

private class RegistrationService : RegistrationServiceCoroutineImplBase() {

    private val daoImpl = DAOFacadeImpl()

    override suspend fun put(request: User): Result =
        with(request) {
            val id = daoImpl.addNewUser(
                lastname, firstname, middlename, age,
                gender.number.toByte()
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

    override suspend fun getMany(request: Register.Id): Register.ManyUsers {
        return super.getMany(request)
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
            UserDao.Gender.MALE -> User.Gender.MALE
            else -> User.Gender.FEMALE
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