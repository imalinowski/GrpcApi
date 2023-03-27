import Register.Result
import Register.User
import RegistrationServiceGrpcKt.RegistrationServiceCoroutineImplBase
import io.grpc.ServerBuilder
import User as UserDao

private class RegistrationService : RegistrationServiceCoroutineImplBase() {

    private val daoImpl = DAOFacadeImpl()

    override suspend fun put(request: User): Result =
        with(request) {
            val success = daoImpl.addNewUser(
                lastname, firstname, middlename, age,
                gender.number.toByte()
            )
            println(
                "Registering user " +
                        "${request.lastname} ${request.firstname} " +
                        "${request.middlename}, age: ${request.age}," +
                        " gender: ${request.gender.name}"
            )
            return result { succeeded = success }
        }

    override suspend fun getMany(request: Register.Id): Register.ManyUsers {
        return super.getMany(request)
    }

    override suspend fun get(request: Register.Id): User {
        val user = daoImpl.getUser(request.id)
        return User.newBuilder().apply {
            lastname = user?.lastname
            firstname = user?.firstname
            middlename = user?.middlename
            age = user?.age ?: 0
            gender = user?.gender?.map()
        }.build()
    }

    fun UserDao.Gender.map(): User.Gender =
        when (this) {
            UserDao.Gender.MALE -> User.Gender.MALE
            else -> User.Gender.FEMALE
        }


    override suspend fun delete(request: Register.Id): Result {
        return super.delete(request)
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