import RegistrationServiceGrpcKt.RegistrationServiceCoroutineStub
import io.grpc.ManagedChannelBuilder

suspend fun main() {
    val port = 50051

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    val stub = RegistrationServiceCoroutineStub(channel)
    val data = user {
        lastname = "Ivanov"
        firstname = "Petr"
        middlename = "Sidorovich"
        age = 23
        gender = Register.User.Gender.MALE
    }
    val result = stub.put(data)
    when {
        result.succeeded -> { /*TODO*/ }
        result.error.isNotEmpty() -> { /*TODO*/ }
        result.isInitialized -> { /*TODO*/ }
    }

    print("Success is ${result.succeeded}")
}