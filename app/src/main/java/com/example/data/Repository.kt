package com.example.data

import kotlinx.coroutines.flow.Flow

class DiverRepository(private val database: AppDatabase) {
    val allLogs: Flow<List<DiveLog>> = database.diveLogDao().getAllLogs()
    val allStudents: Flow<List<StudentCertification>> = database.studentCertificationDao().getAllStudents()
    val allGear: Flow<List<PersonalGear>> = database.personalGearDao().getAllGear()
    val allTours: Flow<List<DiveTour>> = database.diveTourDao().getAllTours()
    val allBuddyPosts: Flow<List<BuddyPost>> = database.buddyPostDao().getAllPosts()

    // Logs operations
    suspend fun insertLog(log: DiveLog) {
        database.diveLogDao().insertLog(log)
    }

    suspend fun deleteLogById(id: Int) {
        database.diveLogDao().deleteLogById(id)
    }

    suspend fun markLogSynced(id: Int) {
        database.diveLogDao().markSynced(id)
    }

    // Students operations
    suspend fun insertStudent(student: StudentCertification) {
        database.studentCertificationDao().insertStudent(student)
    }

    suspend fun deleteStudentById(id: Int) {
        database.studentCertificationDao().deleteStudentById(id)
    }

    // Gear operations
    suspend fun insertGear(gear: PersonalGear) {
        database.personalGearDao().insertGear(gear)
    }

    suspend fun deleteGearById(id: Int) {
        database.personalGearDao().deleteGearById(id)
    }

    // Tours operations
    suspend fun insertTour(tour: DiveTour) {
        database.diveTourDao().insertTour(tour)
    }

    suspend fun deleteTourById(id: Int) {
        database.diveTourDao().deleteTourById(id)
    }

    // Buddy Posts operations
    suspend fun insertBuddyPost(post: BuddyPost) {
        database.buddyPostDao().insertPost(post)
    }

    suspend fun deleteBuddyPostById(id: Int) {
        database.buddyPostDao().deletePostById(id)
    }
}
