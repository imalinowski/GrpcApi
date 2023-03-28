import Register.User.Gender.FEMALE
import Register.User.Gender.MALE
import RegistrationServiceGrpcKt.RegistrationServiceCoroutineStub
import io.grpc.ManagedChannelBuilder

const val PORT = 50051
private val channel = ManagedChannelBuilder.forAddress("localhost", PORT)
    .usePlaintext().build()
private val stub = RegistrationServiceCoroutineStub(channel)

suspend fun main() {
    while (true) {
        val result = when (readln().lowercase().trim()) {
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
    return stub.delete(id { id = readln("ID :").toInt() })
}

private suspend fun putUser(): Register.Result {
    return stub.put(
        user {
            lastname = readln("LastName :")
            firstname = readln("FirstName :")
            middlename = readln("MiddleName :")
            age = readln("Age : ").toInt()
            gender = when (readln("Gender :").trim().lowercase()) {
                "male", "0" -> MALE
                else -> FEMALE
            }
        }
    )
}

private suspend fun getUser(): Register.Result {
    val user = stub.get(
        id { id = readln("ID").toInt() }
    )
    return result {
        val initialized = user.firstname != ""
        succeeded = initialized
        if (initialized) {
            message = user.toString()
        } else {
            error = "User Not Found"
        }
    }
}

private suspend fun getManyUsers(): Register.Result {
    return Register.Result.newBuilder().apply {
        succeeded = false
    }.build()
}

private fun readln(message: String): String {
    println(message)
    return readln()
}