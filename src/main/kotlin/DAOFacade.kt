interface DAOFacade {
    suspend fun getUser(id: Int): User?
    suspend fun addNewUser(title: String, body: String): User?
    suspend fun deleteArticle(id: Int): Boolean
    suspend fun allUsers(): List<User>
}

class DAOFacadeImpl : DAOFacade {
//    private fun resultRowToArticle(row: ResultRow) = Article(
//        id = row[Articles.id],
//        title = row[Articles.title],
//        body = row[Articles.body],
//    )
//
//    override suspend fun allArticles(): List<Article> = dbQuery {
//        Articles.selectAll().map(::resultRowToArticle)
//    }

    override suspend fun getUser(id: Int): User? {

        TODO("Not yet implemented")
    }

    override suspend fun addNewUser(title: String, body: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArticle(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun allUsers(): List<User> {
        TODO("Not yet implemented")
    }
}