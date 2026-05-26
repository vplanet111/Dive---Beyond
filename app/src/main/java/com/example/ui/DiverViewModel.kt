package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Chat Massage model for simulation
data class ChatMessage(
    val sender: String,
    val text: String,
    val time: String,
    val isInstructor: Boolean = false,
    val isMe: Boolean = false
)

class DiverViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = DiverRepository(database)

    // Flow integration for Room items
    val logs: StateFlow<List<DiveLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<StudentCertification>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gears: StateFlow<List<PersonalGear>> = repository.allGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tours: StateFlow<List<DiveTour>> = repository.allTours
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val buddyPosts: StateFlow<List<BuddyPost>> = repository.allBuddyPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI State variables
    private val _selectedTab = MutableStateFlow(0) // 0: Dashboard, 1: Logbook, 2: Instructor, 3: Tours/Booking, 4: Comm/Chat
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _locale = MutableStateFlow("ko") // "ko" or "en"
    val locale: StateFlow<String> = _locale.asStateFlow()

    private val _usdToKrwRate = MutableStateFlow(1365.50) // Exchange rates simulated
    val usdToKrwRate: StateFlow<Double> = _usdToKrwRate.asStateFlow()

    private val _sosActive = MutableStateFlow(false)
    val sosActive: StateFlow<Boolean> = _sosActive.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Guided checklists & safety manuals
    private val _gearChecklist = MutableStateFlow(
        listOf(
            "Regulator inspection & HP hose leak check" to false,
            "BCD inflator test & dump valves pull" to false,
            "Dive Computer battery & altitude mode" to false,
            "Mask strap, snorkel seal, and anti-fog" to false,
            "Fins strap & spare buckles overview" to false,
            "Weight belt buckle quick-release verify" to false,
            "Emergency signaling (SMB/Spool) and whistle check" to false,
            "Emergency O2 kit & first-aid kit in boat" to false
        )
    )
    val gearChecklist: StateFlow<List<Pair<String, Boolean>>> = _gearChecklist.asStateFlow()

    // Real-time Chat Simulation State
    private val _groupChats = MutableStateFlow(
        listOf(
            ChatMessage("Yuna Park", "Is anyone open to a dive in Saesum Jeju tomorrow?", "09:30 AM"),
            ChatMessage("Jiwoo Park", "Jeju Munseom wave and surfs are high today, but tomorrow looks calm!", "10:15 AM", isMe = true),
            ChatMessage("Instructor Lee", "Safety first tomorrow. Always bring SMB and stay with your buddies.", "10:35 AM", isInstructor = true)
        )
    )
    val groupChats: StateFlow<List<ChatMessage>> = _groupChats.asStateFlow()

    private val _instructorChats = MutableStateFlow(
        listOf(
            ChatMessage("Instructor Lee", "Welcome Minjun! In Open Water Section 3, make sure your mask-clearing skill is smooth.", "Yesterday", isInstructor = true),
            ChatMessage("Minjun Kim", "Thanks Instructor! I kept inhaling water through my nose. Will try tilting my head back.", "Yesterday", isMe = true)
        )
    )
    val instructorChats: StateFlow<List<ChatMessage>> = _instructorChats.asStateFlow()

    fun updateTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun toggleLocale() {
        _locale.value = if (_locale.value == "ko") "en" else "ko"
    }

    fun updateExchangeRate(newRate: Double) {
        _usdToKrwRate.value = newRate
    }

    fun toggleSos() {
        _sosActive.value = !_sosActive.value
    }

    fun toggleChecklistItem(index: Int) {
        val currentList = _gearChecklist.value.toMutableList()
        val item = currentList[index]
        currentList[index] = Pair(item.first, !item.second)
        _gearChecklist.value = currentList
    }

    // Convert values
    fun formatCurrency(amountUsd: Double): String {
        return if (_locale.value == "ko") {
            val krw = amountUsd * _usdToKrwRate.value
            String.format("%,.0f원", krw)
        } else {
            String.format("$%,.2f", amountUsd)
        }
    }

    // Synchronize local data to cloud
    fun triggerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            kotlinx.coroutines.delay(1800) // simulated network delay
            logs.value.forEach { log ->
                if (!log.isSynced) {
                    repository.markLogSynced(log.id)
                }
            }
            _isSyncing.value = false
        }
    }

    // CRUD - Create log (Limit to 3 photos tags "사진을 업로드시 3개 제한")
    fun addDiveLog(
        location: String,
        depth: Double,
        duration: Int,
        visibility: String,
        temp: Double,
        notes: String,
        rating: Int,
        photos: List<String>,
        isBuddy: Boolean,
        lat: Double = 33.2201 + (Math.random() - 0.5) * 0.1,
        lng: Double = 126.5623 + (Math.random() - 0.5) * 0.1
    ) {
        viewModelScope.launch {
            // Respecting photo limit strictly
            val limitedPhotos = photos.take(3)
            val photosJson = "[" + limitedPhotos.joinToString(",") { "\"$it\"" } + "]"

            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = df.format(Date())

            val newLog = DiveLog(
                date = todayStr,
                location = location,
                depthMeters = depth,
                bottomTimeMinutes = duration,
                visibility = visibility,
                temperatureCelsius = temp,
                notes = notes,
                rating = rating,
                photoUrlsJson = photosJson,
                latitude = lat,
                longitude = lng,
                isSynced = false, // starts offline, triggers sync later
                isBuddyRecruitment = isBuddy,
                instructorFeedback = "Pending instructor review."
            )
            repository.insertLog(newLog)

            // If buddy recruitment tagged, push it also to Buddy Recruitment board!
            if (isBuddy) {
                val newBuddyPost = BuddyPost(
                    diverName = "Myself (Level 4)",
                    diverLevel = "Advanced Open Water",
                    title = "Join me at $location!",
                    destination = location,
                    date = todayStr,
                    maxBuddies = 2,
                    currentBuddies = 0,
                    contactInfo = "DiverHub App Chat",
                    notes = "Created from logbook. Logged depth: $depth meters. Let's explore together!"
                )
                repository.insertBuddyPost(newBuddyPost)
            }
        }
    }

    fun deleteLog(id: Int) {
        viewModelScope.launch {
            repository.deleteLogById(id)
        }
    }

    // CRUD - Student management
    fun addStudent(name: String, email: String, certName: String, initialProgress: Int = 10) {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = df.format(Date())

            val student = StudentCertification(
                studentName = name,
                studentEmail = email,
                certificateName = certName,
                status = "In Progress",
                progressPercent = initialProgress,
                issueDate = dateStr,
                trainingLogSharing = "Shared: Academic quizzes completed.",
                feedbackFromInstructor = "Assigned exercises. Keep up the safety logs."
            )
            repository.insertStudent(student)
        }
    }

    fun updateStudentProgress(id: Int, title: String, progress: Int, status: String, feedback: String, trainingLog: String) {
        viewModelScope.launch {
            students.value.find { it.id == id }?.let { original ->
                val updated = original.copy(
                    certificateName = title,
                    progressPercent = progress,
                    status = status,
                    feedbackFromInstructor = feedback,
                    trainingLogSharing = trainingLog
                )
                repository.insertStudent(updated)
            }
        }
    }

    fun deleteStudent(id: Int) {
        viewModelScope.launch {
            repository.deleteStudentById(id)
        }
    }

    // CRUD - Gear inventory
    fun addGear(name: String, category: String, priceUsd: Double, serviceIntervalMonths: Int = 12) {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            val buyDate = df.format(cal.time)
            cal.add(Calendar.MONTH, serviceIntervalMonths)
            val checkDate = df.format(cal.time)

            val gear = PersonalGear(
                name = name,
                category = category,
                purchaseDate = buyDate,
                purchasePriceUsd = priceUsd,
                lastCheckDate = buyDate,
                nextCheckDate = checkDate,
                status = "Normal"
            )
            repository.insertGear(gear)
        }
    }

    fun checkUpGear(id: Int) {
        viewModelScope.launch {
            gears.value.find { it.id == id }?.let { original ->
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val cal = Calendar.getInstance()
                val todayStr = df.format(cal.time)
                cal.add(Calendar.MONTH, 12)
                val updated = original.copy(
                    lastCheckDate = todayStr,
                    nextCheckDate = df.format(cal.time),
                    status = "Normal"
                )
                repository.insertGear(updated)
            }
        }
    }

    fun deleteGear(id: Int) {
        viewModelScope.launch {
            repository.deleteGearById(id)
        }
    }

    // CRUD - Buddy Recruiting ("버디 구함")
    fun addBuddyPost(title: String, destination: String, maxBuddies: Int, contact: String, notes: String) {
        viewModelScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = df.format(Date())
            val post = BuddyPost(
                diverName = "Self Expert",
                diverLevel = "rescue Master",
                title = title,
                destination = destination,
                date = todayStr,
                maxBuddies = maxBuddies,
                currentBuddies = 0,
                contactInfo = contact,
                notes = notes
            )
            repository.insertBuddyPost(post)
        }
    }

    fun joinBuddyPost(id: Int) {
        viewModelScope.launch {
            buddyPosts.value.find { it.id == id }?.let { post ->
                if (post.currentBuddies < post.maxBuddies) {
                    val updated = post.copy(currentBuddies = post.currentBuddies + 1)
                    repository.insertBuddyPost(updated)
                }
            }
        }
    }

    fun deleteBuddyPost(id: Int) {
        viewModelScope.launch {
            repository.deleteBuddyPostById(id)
        }
    }

    // Tours & Booking
    fun bookTour(id: Int) {
        viewModelScope.launch {
            tours.value.find { it.id == id }?.let { tour ->
                val currentParticipants = if (tour.participantsJson.isEmpty() || tour.participantsJson == "[]") {
                    mutableListOf()
                } else {
                    tour.participantsJson
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")
                        .map { it.trim().replace("\"", "") }
                        .toMutableList()
                }

                if (!tour.isBooked && currentParticipants.size < tour.maxParticipants) {
                    currentParticipants.add("Myself (You)")
                    val participantsJson = "[" + currentParticipants.joinToString(",") { "\"$it\"" } + "]"

                    // Update database
                    val updated = tour.copy(
                        isBooked = true,
                        participantsJson = participantsJson,
                        expensesUsd = tour.expensesUsd + tour.costUsd // increase spendings of tour budget
                    )
                    repository.insertTour(updated)
                }
            }
        }
    }

    fun cancelTour(id: Int) {
        viewModelScope.launch {
            tours.value.find { it.id == id }?.let { tour ->
                val currentParticipants = if (tour.participantsJson.isEmpty() || tour.participantsJson == "[]") {
                    mutableListOf()
                } else {
                    tour.participantsJson
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")
                        .map { it.trim().replace("\"", "") }
                        .toMutableList()
                }

                if (tour.isBooked) {
                    currentParticipants.remove("Myself (You)")
                    val participantsJson = "[" + currentParticipants.joinToString(",") { "\"$it\"" } + "]"

                    val updated = tour.copy(
                        isBooked = false,
                        participantsJson = participantsJson,
                        expensesUsd = (tour.expensesUsd - tour.costUsd).coerceAtLeast(0.0)
                    )
                    repository.insertTour(updated)
                }
            }
        }
    }

    fun updateTourFinance(id: Int, budgetUsd: Double, expensesUsd: Double) {
        viewModelScope.launch {
            tours.value.find { it.id == id }?.let { tour ->
                val updated = tour.copy(budgetUsd = budgetUsd, expensesUsd = expensesUsd)
                repository.insertTour(updated)
            }
        }
    }

    fun rateTour(id: Int, reviewText: String, rating: Int) {
        viewModelScope.launch {
            tours.value.find { it.id == id }?.let { tour ->
                // Add photos dynamically to participants photo gallery too!
                val currentPhotos = if (tour.photosJson.isEmpty() || tour.photosJson == "[]") {
                    mutableListOf()
                } else {
                    tour.photosJson
                        .replace("[", "")
                        .replace("]", "")
                        .split(",")
                        .map { it.trim().replace("\"", "") }
                        .toMutableList()
                }
                currentPhotos.add("user_upload_gallery")

                val updated = tour.copy(
                    reviewText = reviewText,
                    reviewRating = rating,
                    photosJson = "[" + currentPhotos.take(3).joinToString(",") { "\"$it\"" } + "]" // Max 3 limit
                )
                repository.insertTour(updated)
            }
        }
    }

    // Simulated messaging in Group or Instructor channel
    fun sendGroupChatMessage(text: String) {
        if (text.isBlank()) return
        val current = _groupChats.value.toMutableList()
        current.add(ChatMessage("Jiwoo Park", text, "Just now", isMe = true))
        _groupChats.value = current
    }

    fun sendInstructorChatMessage(text: String) {
        if (text.isBlank()) return
        val current = _instructorChats.value.toMutableList()
        current.add(ChatMessage("Student", text, "Just now", isMe = true))
        _instructorChats.value = current

        // Auto instructor feedback reply simulation!
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            val replyingList = _instructorChats.value.toMutableList()
            replyingList.add(
                ChatMessage(
                    "Instructor Lee",
                    "Received your request. Excellent approach. I will log this progress in your dashboard certification status immediately!",
                    "Just now",
                    isInstructor = true
                )
            )
            _instructorChats.value = replyingList
        }
    }
}
