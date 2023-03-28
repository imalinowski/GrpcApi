import RegistrationServiceGrpcKt.RegistrationServiceCoroutineStub
import io.grpc.ManagedChannelBuilder

private val port = 50051
private val channel = ManagedChannelBuilder.forAddress("localhost", port)
    .usePlaintext().build()
private val stub = RegistrationServiceCoroutineStub(channel)

suspend fun main() {
    while (true) {
        val result = when (readln()) {
            "put" -> putUser()
            "get" -> getUser()
            "getMany" -> getManyUsers()
            "delete" -> deleteUser()
            else -> {
                Register.Result.newBuilder().apply {
                    succeeded = false
                    error = "Unknown Command!"
                }.build()
            }
        }
        if (result.succeeded) {
            println("Success\n${result.message}")
        } else {
            println("Error ${result.error}")
        }
    }
}

private suspend fun deleteUser(): Register.Result {
    return Register.Result.newBuilder().apply {
        succeeded = false
        error = "NotImplemented"
    }.build()
}

private suspend fun putUser(): Register.Result {
    print("LastName :")
    val lastname = readln()
    print("FirstName :")
    val firstname = readln()
    print("MiddleName :")
    val middlename = readln()
    print("Age :")
    val age = readln().toInt()
    print("Gender :")
    val gender = when (readln()) {
        "male" -> Register.User.Gender.MALE
        else -> Register.User.Gender.FEMALE
    }
    return stub.put(
        user {
            this.lastname = lastname
            this.firstname = firstname
            this.middlename = middlename
            this.age = age
            this.gender = gender
        }
    )
}

private suspend fun getUser(): Register.Result {
    val user =  stub.get(
        Register.Id.newBuilder().apply {
            id = let {
                println("ID")
                readln().toInt()
            }
        }.build()
    )
    return Register.Result.newBuilder().apply {
        val inited = user.firstname != ""
        succeeded = inited
        if (inited) {
            message = user.toString()
        } else {
            error = "User Not Found"
        }
    }.build()
}

private suspend fun getManyUsers(): Register.Result {
    return Register.Result.newBuilder().apply {
        succeeded = false
    }.build()
}