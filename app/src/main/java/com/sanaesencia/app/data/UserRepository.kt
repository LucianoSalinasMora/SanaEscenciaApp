package com.sanaesencia.app.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    // Usuario administrador por defecto
    private val defaultAdmin = UserEntity(
        run = "admin",
        name = "Administrador Centro Sanaesencia",
        email = "admin@sanaesencia.cl",
        phone = "+56900000000",
        passwordHash = "admin",
        role = UserRole.ADMIN,
        securityQuestion = "Pregunta Admin",
        securityAnswer = "admin"
    )

    // Carga los usuarios iniciales (profesionales de la web y paciente de prueba) si aún no existen
    suspend fun seedInitialData() {
        seedUsers.forEach { user ->
            if (userDao.getUserByRun(user.run) == null) {
                userDao.insertUser(user)
            }
        }
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(run: String) {
        userDao.deleteUser(run)
    }

    suspend fun login(run: String, password: String): UserEntity? {
        val cleanRun = run.trim()
        val formattedRun = cleanRun.replace(".", "").replace("-", "").uppercase()

        if (cleanRun.equals("admin", ignoreCase = true) && password == "admin") {
            try {
                userDao.insertUser(defaultAdmin)
            } catch (e: Exception) {
                // Ignore constraint exceptions if admin exists
            }
            return defaultAdmin
        }

        // Busca por RUN directo o RUN formateado sin puntos/guión
        return userDao.login(cleanRun, password)
            ?: userDao.login(formattedRun, password)
    }

    suspend fun getUserByRun(run: String): UserEntity? {
        val cleanRun = run.trim()
        val formattedRun = cleanRun.replace(".", "").replace("-", "").uppercase()

        if (cleanRun.equals("admin", ignoreCase = true)) {
            return defaultAdmin
        }

        return userDao.getUserByRun(cleanRun)
            ?: userDao.getUserByRun(formattedRun)
    }

    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>> {
        return userDao.getUsersByRole(role)
    }

    suspend fun updatePassword(run: String, newPassword: String) {
        val cleanRun = run.trim()
        val formattedRun = cleanRun.replace(".", "").replace("-", "").uppercase()
        userDao.updatePassword(cleanRun, newPassword)
        userDao.updatePassword(formattedRun, newPassword)
    }
}
