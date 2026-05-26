package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dive_logs")
data class DiveLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val location: String,
    val depthMeters: Double,
    val bottomTimeMinutes: Int,
    val visibility: String,
    val temperatureCelsius: Double,
    val notes: String,
    val rating: Int,
    val photoUrlsJson: String = "[]", // List of up to 3 photo strings
    val latitude: Double,
    val longitude: Double,
    val isSynced: Boolean = true, // To support the requested offline mode with sync flag
    val instructorFeedback: String = "", // Instructor feedback system
    val isBuddyRecruitment: Boolean = false, // Linked to "버디 구함" recruitment system
    val weatherCondition: String = "Calm"
)

@Entity(tableName = "student_certifications")
data class StudentCertification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val studentEmail: String,
    val certificateName: String, // e.g., Open Water, Advanced, Rescue
    val status: String, // Enrolled, In Progress, Review, Approved, Issued
    val progressPercent: Int, // 0 - 100 for visual dashboard
    val issueDate: String,
    val trainingLogSharing: String = "", // Shared logs between instructor and student
    val feedbackFromInstructor: String = "" // Real-time feedback
)

@Entity(tableName = "personal_gear")
data class PersonalGear(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Regulator, Computer, BCD, Wetsuit, Fins, etc.
    val serialNumber: String = "",
    val purchaseDate: String,
    val purchasePriceUsd: Double, // Price in USD (will support live exchange rate to KRW)
    val lastCheckDate: String,
    val nextCheckDate: String, // Periodic inspect notifications
    val status: String = "Normal" // Good, Needs Service, Repairing
)

@Entity(tableName = "dive_tours")
data class DiveTour(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val destination: String,
    val date: String,
    val costUsd: Double,
    val budgetUsd: Double, // For tour financial sheet
    val expensesUsd: Double, // Real spent expenses
    val maxParticipants: Int,
    val description: String,
    val participantsJson: String = "[]", // Simulated participant list
    val photosJson: String = "[]", // Shared gallery photos
    val isBooked: Boolean = false, // Booked and payment done status
    val reviewText: String = "",
    val reviewRating: Int = 0
)

@Entity(tableName = "buddy_posts")
data class BuddyPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diverName: String,
    val diverLevel: String,
    val title: String,
    val destination: String,
    val date: String,
    val maxBuddies: Int,
    val currentBuddies: Int,
    val contactInfo: String,
    val notes: String
)
