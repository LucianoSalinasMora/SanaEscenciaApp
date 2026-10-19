package com.sanaesencia.app.data

import kotlinx.coroutines.flow.Flow

class AboutRepository(private val aboutDao: AboutDao) {
    val aboutInfo: Flow<AboutInfoEntity?> = aboutDao.getAboutInfo()

    suspend fun saveAboutInfo(info: AboutInfoEntity) {
        aboutDao.insertAboutInfo(info)
    }
}
