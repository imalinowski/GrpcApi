import Register.Result
import Register.User
import RegistrationServiceGrpcKt.RegistrationServiceCoroutineImplBase
import io.grpc.ServerBuilder

private class RegistrationService : RegistrationServiceCoroutineImplBase() {
    override suspend fun put(request: User): Result {
        println(
            "Registering user ${request.lastname} ${request.firstname} " +
                    "${request.middlename}, age: ${request.age}, gender: ${request.gender.name}"
        )
        return result { succeeded = true }
    }

    override suspend fun getMany(request: Register.Id): Register.ManyUsers {
        return super.getMany(request)
    }

    override suspend fun get(request: Register.Id): User {
        return super.get(request)
    }

    override suspend fun delete(request: Register.Id): Result {
        return super.delete(request)
    }
}

fun main() {
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