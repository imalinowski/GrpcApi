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
            User.newBuilder().apply {
                lastname = user?.lastname
                firstname = user?.firstname
                middlename = user?.middlename
                age = user?.age ?: 0
                gender = user?.gender?.map()
            }.build()
        } catch (t: Throwable) {
            User.newBuilder().apply {
                lastname = ""
            }.build()
        }

    override suspend fun delete(request: Register.Id): Result {
        return super.delete(request)
    }

    private fun UserDao.Gender.map(): User.Gender =
        when (this) {
            UserDao.Gender.MALE -> User.Gender.MALE
            else -> User.Gender.FEMALE
        }

}

fun main() {
    DatabaseFactory.init()

    val port = 50051
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