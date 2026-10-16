package com.sanaesencia.app.data

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun login(run: String, password: String): UserEntity? {
        return userDao.login(run, password)
    }

    suspend fun getUserByRun(run: String): UserEntity? {
        return userDao.getUserByRun(run)
    }

    suspend fun updatePassword(run: String, newPassword: String) {
        userDao.updatePassword(run, newPassword)
    }
}

