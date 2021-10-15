package com.khedr.runtracking.domain.repository

import com.khedr.runtracking.data.local.RunDao
import com.khedr.runtracking.data.local.entity.RunEntity
import com.khedr.runtracking.data.repository.RunRepository
import javax.inject.Inject

class RunRepositoryImpl
@Inject constructor(
    private val runDao: RunDao
) : RunRepository {
    override suspend fun insertRun(run: RunEntity) {
        runDao.insertRun(run)
    }

    override suspend fun deleteRun(run: RunEntity) {
        runDao.deleteRun(run)
    }

    override fun getAllRunsSortedBy(columnName: String) = runDao.getAllRunsSortedBy(columnName)

    override fun getTotalOf(columnName: String) = runDao.getTotalOf(columnName)

    override fun getAvgOf(columnName: String) = runDao.getAvgOf(columnName)


}