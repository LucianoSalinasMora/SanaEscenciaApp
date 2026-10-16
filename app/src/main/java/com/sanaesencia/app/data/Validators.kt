package com.sanaesencia.app.data

object Validators {

    // Validador de RUN Chileno (Módulo 11)
    fun isValidRUN(run: String): Boolean {
        val cleanRun = run.replace(".", "").replace("-", "").uppercase()
        if (cleanRun.length < 8) return false

        val rut = cleanRun.substring(0, cleanRun.length - 1)
        val dv = cleanRun.substring(cleanRun.length - 1)

        var sum = 0
        var multiplier = 2

        try {
            for (i in rut.length - 1 downTo 0) {
                sum += rut[i].toString().toInt() * multiplier
                multiplier = if (multiplier == 7) 2 else multiplier + 1
            }
            val expectedDv = 11 - (sum % 11)
            val finalDv = when (expectedDv) {
                11 -> "0"
                10 -> "K"
                else -> expectedDv.toString()
            }
            return dv == finalDv
        } catch (e: Exception) {
            return false
        }
    }

    // Validador simple de teléfono
    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[+]?[0-9]{8,12}\$"))
    }
}