package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

@Dao
interface DiveLogDao {
    @Query("SELECT * FROM dive_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DiveLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DiveLog)

    @Query("UPDATE dive_logs SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("DELETE FROM dive_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)
}

@Dao
interface StudentCertificationDao {
    @Query("SELECT * FROM student_certifications ORDER BY studentName ASC")
    fun getAllStudents(): Flow<List<StudentCertification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentCertification)

    @Query("DELETE FROM student_certifications WHERE id = :id")
    suspend fun deleteStudentById(id: Int)
}

@Dao
interface PersonalGearDao {
    @Query("SELECT * FROM personal_gear ORDER BY name ASC")
    fun getAllGear(): Flow<List<PersonalGear>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGear(gear: PersonalGear)

    @Query("DELETE FROM personal_gear WHERE id = :id")
    suspend fun deleteGearById(id: Int)
}

@Dao
interface DiveTourDao {
    @Query("SELECT * FROM dive_tours ORDER BY date DESC")
    fun getAllTours(): Flow<List<DiveTour>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTour(tour: DiveTour)

    @Query("DELETE FROM dive_tours WHERE id = :id")
    suspend fun deleteTourById(id: Int)
}

@Dao
interface BuddyPostDao {
    @Query("SELECT * FROM buddy_posts ORDER BY date DESC")
    fun getAllPosts(): Flow<List<BuddyPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: BuddyPost)

    @Query("DELETE FROM buddy_posts WHERE id = :id")
    suspend fun deletePostById(id: Int)
}

@Database(
    entities = [
        DiveLog::class,
        StudentCertification::class,
        PersonalGear::class,
        DiveTour::class,
        BuddyPost::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diveLogDao(): DiveLogDao
    abstract fun studentCertificationDao(): StudentCertificationDao
    abstract fun personalGearDao(): PersonalGearDao
    abstract fun diveTourDao(): DiveTourDao
    abstract fun buddyPostDao(): BuddyPostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diver_hub_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate initial data on a background thread
                Executors.newSingleThreadExecutor().execute {
                    // Dive Logs
                    db.execSQL(
                        "INSERT INTO dive_logs (date, location, depthMeters, bottomTimeMinutes, visibility, temperatureCelsius, notes, rating, photoUrlsJson, latitude, longitude, isSynced, instructorFeedback, isBuddyRecruitment, weatherCondition) VALUES " +
                                "('2026-05-10', 'Jeju Seogwipo Moon Island', 25.4, 42, '15m Clear', 18.5, 'Stunning coral reefs. We saw an octopus and schools of damselfish!', 5, '[\"jeju1\", \"jeju2\"]', 33.2201, 126.5623, 1, 'Great neutral buoyancy control in current. Keep maintaining safety margins!', 0, 'Sunny'), " +
                                "('2026-05-18', 'Okinawa Blue Cave', 18.2, 48, '22m Pristine', 23.0, 'Beautiful glowing blue water inside the cave system. Perfect for underwater selfies.', 4, '[\"okinawa_cave\"]', 26.4428, 127.7719, 1, 'Careful with ascending rate inside overhead environment. Excellent dive.', 1, 'Cloudy'), " +
                                "('2026-05-25', 'Panglao Island (Balicasag)', 30.1, 35, '25m Warm', 27.5, 'Fantastic deep wall dive. Turtles everywhere!', 5, '[]', 9.5186, 123.6841, 0, 'Excellent trim. Good deep air consumption management.', 0, 'Calm')"
                    )

                    // Student Certifications
                    db.execSQL(
                        "INSERT INTO student_certifications (studentName, studentEmail, certificateName, status, progressPercent, issueDate, trainingLogSharing, feedbackFromInstructor) VALUES " +
                                "('Minjun Kim', 'minjun@gmail.com', 'PADI Open Water Diver', 'In Progress', 75, '2026-06-15', 'Completed pool skills and 2 ocean training sessions.', 'Great recovery of regulator. Work on mask clearing without panic next session.'), " +
                                "('Jiwoo Park', 'jiwoo@naver.com', 'SSI Advanced Adventurer', 'Issued', 100, '2026-05-12', 'Shared: Completed navigation, night dive, deep dive, wreck specialty logs.', 'Approved! Passed water map verification with flying colors. Certification official.'), " +
                                "('David Smith', 'david@gmail.com', 'PADI Rescue Diver', 'Enrolled', 20, '2026-06-30', 'Academic exam pre-review completed.', 'First emergency response simulation starts this weekend. Bring thermal suit.')"
                    )

                    // Personal Gear
                    db.execSQL(
                        "INSERT INTO personal_gear (name, category, serialNumber, purchaseDate, purchasePriceUsd, lastCheckDate, nextCheckDate, status) VALUES " +
                                "('Scubapro MK25 EVO Regulator', 'Regulator', 'SN-98213-GP', '2025-01-15', 799.0, '2026-01-10', '2027-01-10', 'Normal'), " +
                                "('Suunto D5 Slate Computer', 'Computer', 'D5-883921-X', '2025-03-22', 950.0, '2026-03-20', '2027-03-20', 'Normal'), " +
                                "('Cressi Carbon BCD', 'BCD', 'CRS-443-B', '2024-06-12', 540.0, '2025-06-12', '2026-06-12', 'Needs Service'), " +
                                "('Gull Vader Mask Black', 'Mask', 'GV32', '2025-07-01', 140.0, '2026-05-01', '2026-11-01', 'Normal')"
                    )

                    // Dive Tours
                    db.execSQL(
                        "INSERT INTO dive_tours (title, destination, date, costUsd, budgetUsd, expensesUsd, maxParticipants, description, participantsJson, photosJson, isBooked) VALUES " +
                                "('Jeju Eco-Coral Expedition 3D2N', 'Jeju Island, Korea', '2026-07-10', 350.0, 3000.0, 1850.0, 12, 'Explore Jeju most remote soft coral gardens of Saesum and Munseom. Price includes boats, transport, tanks, and master fees.', '[\"Yuna Park\", \"Hajun Lee\", \"Sarah Conner\"]', '[\"jeju_spot1\", \"jeju_spot2\"]', 0), " +
                                "('Panglao Sanctuary Deep Explorer', 'Bohol, Philippines', '2026-08-15', 850.0, 8000.0, 4200.0, 8, 'Premium Bohol tour covering Balicasag Sanctuary wall dives, turtle watching, and Alona house reef dives. 6 boat dives + 2 night shore dives.', '[\"Minjun Kim\", \"Thomas Wayne\"]', '[\"bohol_wall\", \"turtle_underwater\"]', 1), " +
                                "('Ishigaki Manta Ray Cruise', 'Okinawa, Japan', '2026-09-02', 1200.0, 12000.0, 0.0, 10, 'Incredible chance to dive alongside giant mantas. Tour includes 5-star resort, chartered vessel, and gourmet dinner parties.', '[]', '[]', 0)"
                    )

                    // Buddy Posts
                    db.execSQL(
                        "INSERT INTO buddy_posts (diverName, diverLevel, title, destination, date, maxBuddies, currentBuddies, contactInfo, notes) VALUES " +
                                "('Yuna Park', 'Advanced (60+ Dives)', 'Looking for Jeju Munseom Buddy on June 1st!', 'Jeju Seogwipo', '2026-06-01', 2, 1, 'Kakao: yuna_diver', 'Looking for an experienced buddy to do drift diving. I have a rental car and can share gas money!'), " +
                                "('James Cooper', 'Rescue Diver', 'Any buddies for night shore dive at Panglao?', 'Alona Beach, Bohol', '2026-08-16', 1, 0, 'WhatsApp: +1 988-299', 'Let do a night dive! I am staying at Alona Tropical. Planning on diving with Captain Scuba shop.')"
                    )
                }
            }
        }
    }
}
