package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*

// Localization Translation Map
object LocalizedStrings {
    val ko = mapOf(
        "app_title" to "다이버 로그 & 강사 허브",
        "nav_dash" to "대시보드",
        "nav_log" to "로그북",
        "nav_inst" to "교육 & 안전",
        "nav_tour" to "투어 예약",
        "nav_comm" to "커뮤니티",
        "level" to "다이버 레벨",
        "total_dives" to "총 다이빙 횟수",
        "next_level" to "다음 레벨까지",
        "sos_btn" to "긴급 구조 (SOS)",
        "sos_msg" to "비상 해상구조 요청이 송출되는 중입니다!",
        "curr_exchange" to "실시간 환율 정보 (1$ 기준)",
        "sync_cloud" to "클라우드 데이터 동기화",
        "syncing" to "클라우드와 로그 기록 동기화 중...",
        "synced" to "데이터 백업 완료 (오동작 방지)",
        "add_log" to "로그 작성",
        "spot_recom" to "추천 다이빙 스팟",
        "gear_check" to "수중 장비 안전 체크리스트",
        "student_cert" to "교육생 자격증 관리",
        "add_student" to "신규 교육생 등록",
        "gear_purchase" to "개인 장비 및 구매 관리",
        "add_gear" to "새 장비 등록",
        "tour_book" to "다이빙 투어 예약",
        "buddy_hunt" to "버디 구함 (동료 매칭)",
        "add_buddy" to "버디 구인글 등록",
        "group_chat" to "투어 단체 채팅",
        "inst_chat" to "강사 1:1 실시간 피드백",
        "photo_limit_msg" to "사진은 최대 3개까지만 업로드 가능합니다.",
        "search_shop" to "주변 다이빙 샵 / 강사 검색",
        "emergency_manual" to "비상 안전 대처 매뉴얼",
        "usd_converted" to "환산율 표시",
        "pay_title" to "시뮬레이션 간편 결제",
        "budget_exp" to "투어 예산 & 지출 관리",
        "reviews" to "리뷰 및 별점"
    )

    val en = mapOf(
        "app_title" to "Diver Log & Instructor Hub",
        "nav_dash" to "Dashboard",
        "nav_log" to "Logbook",
        "nav_inst" to "Training & Gear",
        "nav_tour" to "Tours/Booking",
        "nav_comm" to "Community",
        "level" to "Diver Level",
        "total_dives" to "Total Dives",
        "next_level" to "Next Level in",
        "sos_btn" to "Emergency SOS",
        "sos_msg" to "Distress signal is broadcasting to nearby vessels!",
        "curr_exchange" to "Live Exchange Rate (Base 1$)",
        "sync_cloud" to "Sync Data to Cloud",
        "syncing" to "Syncing logs to the cloud...",
        "synced" to "Cloud Sync of records successful!",
        "add_log" to "Add Log",
        "spot_recom" to "Recommended Dive Spots",
        "gear_check" to "Underwater Safety Checklist",
        "student_cert" to "Student Certifications",
        "add_student" to "Register New Student",
        "gear_purchase" to "Personal Gear & Purchase Log",
        "add_gear" to "Register New Gear",
        "tour_book" to "Dive Tours Booking",
        "buddy_hunt" to "Find Diving Buddies",
        "add_buddy" to "Post Buddy Hunt",
        "group_chat" to "Tour Group Chat",
        "inst_chat" to "1:1 Instructor Feedback",
        "photo_limit_msg" to "Up to 3 photos limit strictly enforced.",
        "search_shop" to "Search Dive Shops & Instructors",
        "emergency_manual" to "Emergency Safety Manual",
        "usd_converted" to "Exchange rates",
        "pay_title" to "Simulated Instant Payment",
        "budget_exp" to "Tour Budget & Expense Tool",
        "reviews" to "Reviews & Ratings"
    )

    fun get(key: String, locale: String): String {
        return if (locale == "ko") ko[key] ?: key else en[key] ?: key
    }
}

@Composable
fun DiverApp(viewModel: DiverViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val locale by viewModel.locale.collectAsStateWithLifecycle()
    val sosActive by viewModel.sosActive.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DiverTopAppBar(
                locale = locale,
                onToggleLocale = { viewModel.toggleLocale() },
                onSyncTrigger = { viewModel.triggerSync() },
                isSyncing = isSyncing
            )
        },
        bottomBar = {
            DiverBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.updateTab(it) },
                locale = locale
            )
        },
        containerColor = OceanBlueAbyss
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(OceanBlueAbyss, Color(0xFF03080F))
                    )
                )
        ) {
            // Main content based on selected tab
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "MainContentTransition"
            ) { tab ->
                when (tab) {
                    0 -> DashboardScreen(viewModel = viewModel, locale = locale)
                    1 -> LogbookScreen(viewModel = viewModel, locale = locale)
                    2 -> InstructorAndSafetyScreen(viewModel = viewModel, locale = locale)
                    3 -> ToursAndBookingScreen(viewModel = viewModel, locale = locale)
                    4 -> CommunityAndChatScreen(viewModel = viewModel, locale = locale)
                }
            }

            // Global SOS alarm overlay with strobe lights
            if (sosActive) {
                SosEmergencyOverlay(
                    locale = locale,
                    onDismiss = { viewModel.toggleSos() }
                )
            }

            // Syncing indicator override
            if (isSyncing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            CircularProgressIndicator(color = AquaCyanNeon, strokeWidth = 4.dp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = LocalizedStrings.get("syncing", locale),
                                color = DeepWhiteText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Simulating encrypted offshore database handshake...",
                                color = MistyGreyText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiverTopAppBar(
    locale: String,
    onToggleLocale: () -> Unit,
    onSyncTrigger: () -> Unit,
    isSyncing: Boolean
) {
    Surface(
        color = OceanSurfaceBlue,
        tonalElevation = 6.dp,
        modifier = Modifier.drawBehind {
            drawLine(
                color = OceanTealDepth,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Waves,
                contentDescription = "App Icon Logo",
                tint = AquaCyanNeon,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = LocalizedStrings.get("app_title", locale),
                color = DeepWhiteText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1.0f)
            )

            // Sync Button
            IconButton(
                onClick = onSyncTrigger,
                enabled = !isSyncing,
                modifier = Modifier.testTag("sync_cloud_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Cloud backup sync",
                    tint = if (isSyncing) MistyGreyText else AquaCyanNeon
                )
            }

            // Regional Localization Language Switcher
            Button(
                onClick = onToggleLocale,
                colors = ButtonDefaults.buttonColors(containerColor = OceanTealDepth),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("lang_toggle_button")
            ) {
                Text(
                    text = if (locale == "ko") "EN" else "한글",
                    color = AquaCyanNeon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DiverBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    locale: String
) {
    NavigationBar(
        containerColor = OceanSurfaceBlue,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.drawBehind {
            drawLine(
                color = OceanTealDepth,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 2f
            )
        }
    ) {
        val menuItems = listOf(
            Triple(0, LocalizedStrings.get("nav_dash", locale), Icons.Default.Dashboard),
            Triple(1, LocalizedStrings.get("nav_log", locale), Icons.Default.Book),
            Triple(2, LocalizedStrings.get("nav_inst", locale), Icons.Default.School),
            Triple(3, LocalizedStrings.get("nav_tour", locale), Icons.Default.DirectionsBoat),
            Triple(4, LocalizedStrings.get("nav_comm", locale), Icons.Default.People)
        )

        menuItems.forEach { (index, title, icon) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OceanBlueAbyss,
                    selectedTextColor = AquaCyanNeon,
                    indicatorColor = AquaCyanNeon,
                    unselectedIconColor = MistyGreyText,
                    unselectedTextColor = MistyGreyText
                ),
                modifier = Modifier.testTag("nav_item_$index")
            )
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(viewModel: DiverViewModel, locale: String) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val usdToKrwRate by viewModel.usdToKrwRate.collectAsStateWithLifecycle()
    var editRateShow by remember { mutableStateOf(false) }
    var inputRateText by remember { mutableStateOf(usdToKrwRate.toString()) }

    // Gamification dynamic level calculation
    val totalDives = logs.size
    val currentLevelName: String
    val currentLevelNum: Int
    val nextLevelDivesRemaining: Int
    val progressFraction: Float

    when {
        totalDives == 0 -> {
            currentLevelName = if (locale == "ko") "거품 부는 초보 (Bubble Blower)" else "Bubble Blower"
            currentLevelNum = 1
            nextLevelDivesRemaining = 1
            progressFraction = 0f
        }
        totalDives in 1..2 -> {
            currentLevelName = if (locale == "ko") "산호초 탐험가 (Reef Explorer)" else "Reef Explorer"
            currentLevelNum = 2
            nextLevelDivesRemaining = 3 - totalDives
            progressFraction = totalDives / 3f
        }
        totalDives in 3..5 -> {
            currentLevelName = if (locale == "ko") "심해 파수꾼 (Deep Sea Sentinel)" else "Deep Sea Sentinel"
            currentLevelNum = 3
            nextLevelDivesRemaining = 6 - totalDives
            progressFraction = (totalDives - 2) / 4f
        }
        else -> {
            currentLevelName = if (locale == "ko") "포세이돈 마스터 (Poseidon Master)" else "Poseidon Master"
            currentLevelNum = 4
            nextLevelDivesRemaining = 0
            progressFraction = 1f
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SOS Button and warnings first for accessibility / instant eye alignment
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, CoralAlertRed.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (locale == "ko") "긴급 안전 구조 시스템" else "Emergency SOS Safety Room",
                            color = CoralAlertRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (locale == "ko") "위험 처할 시 즉각 호출국 송신 가능" else "Signal distress immediately in danger",
                            color = MistyGreyText,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.toggleSos() },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAlertRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("sos_emergency_button")
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "SOS", tint = DeepWhiteText)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LocalizedStrings.get("sos_btn", locale),
                            color = DeepWhiteText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Gamification Progression Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OceanTealDepth)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = LocalizedStrings.get("level", locale),
                                color = MarineTealBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lv.$currentLevelNum - $currentLevelName",
                                color = AquaCyanNeon,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = "Gold Medal",
                            tint = MarineGoldAccent,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = AquaCyanNeon,
                        trackColor = OceanTealDepth
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${LocalizedStrings.get("total_dives", locale)}: $totalDives",
                            color = DeepWhiteText,
                            fontSize = 12.sp
                        )
                        if (nextLevelDivesRemaining > 0) {
                            Text(
                                text = "${LocalizedStrings.get("next_level", locale)}: $nextLevelDivesRemaining",
                                color = MistyGreyText,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = if (locale == "ko") "최고 레벨 마스터 달성!" else "Max Level Poseidon reached!",
                                color = GlowGreenClean,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Exchange Rates and Currency Convertor Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OceanTealDepth)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = "Exchange",
                                tint = MarineTealBright
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LocalizedStrings.get("curr_exchange", locale),
                                color = DeepWhiteText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        IconButton(onClick = { editRateShow = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Exchange rate", tint = AquaCyanNeon)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OceanTealDepth.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "USD ($)", color = MistyGreyText, fontSize = 11.sp)
                            Text(text = "1.00 USD", color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "swap arrow",
                            tint = AquaCyanNeon,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "KRW (₩)", color = MistyGreyText, fontSize = 11.sp)
                            Text(
                                text = String.format("%,.1f원", usdToKrwRate),
                                color = AquaCyanNeon,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (locale == "ko") "* 모든 장비 및 투어 비용은 실시간으로 한화 및 달러 환율을 고려하여 표시됩니다."
                               else "* Equipment and tour expenditures automatically convert based on this setting.",
                        color = MistyGreyText,
                        fontSize = 11.sp,
                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            }
        }

        // Recommended Places nearby
        item {
            Text(
                text = "${LocalizedStrings.get("spot_recom", locale)} (GeoGPS)",
                color = AquaCyanNeon,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            val spots = listOf(
                Triple("Jeju Seogwipo Munseom", "33.2201, 126.5623", "Soft reef corals, visibility awesome right now"),
                Triple("Okinawa Sunabe Sea wall", "26.3265, 127.7479", "Acropora coral forest, friendly damselfishes"),
                Triple("Panglao Balicasag wall", "9.5186, 123.6841", "Massive green turtles, barracuda schooling, warm water"),
                Triple("East Sea Donghae Port", "37.5245, 129.1141", "Cold water drysuit diving adventure")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(spots) { (name, coords, desc) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OceanTealDepth),
                        modifier = Modifier.width(220.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Spot location",
                                    tint = MarineTealBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = name,
                                    color = DeepWhiteText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "GPS: $coords",
                                color = AquaCyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = desc,
                                color = MistyGreyText,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats visual board
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OceanTealDepth)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (locale == "ko") "통계 및 환경 요약" else "Telemetry Logs & Env Report",
                        color = DeepWhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(OceanTealDepth.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (locale == "ko") "최대 수심" else "Max Depth", color = MistyGreyText, fontSize = 10.sp)
                            Text(text = if (totalDives > 0) "${logs.maxOfOrNull { it.depthMeters } ?: 0.0}m" else "0m", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(OceanTealDepth.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (locale == "ko") "누적 시간" else "Total Time", color = MistyGreyText, fontSize = 10.sp)
                            val totalTime = if (totalDives > 0) logs.sumOf { it.bottomTimeMinutes } else 0
                            Text(text = "${totalTime} min", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(OceanTealDepth.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (locale == "ko") "수리 장비" else "Gears Active", color = MistyGreyText, fontSize = 10.sp)
                            Text(text = "4 EA", color = GlowGreenClean, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    // Exchange rate modal
    if (editRateShow) {
        Dialog(onDismissRequest = { editRateShow = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (locale == "ko") "환율 직접 수정" else "Modify USD/KRW Exchange Rate",
                        color = DeepWhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = inputRateText,
                        onValueChange = { inputRateText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AquaCyanNeon,
                            unfocusedBorderColor = OceanTealDepth,
                            focusedTextColor = DeepWhiteText,
                            unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("rate_input_field")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { editRateShow = false }) {
                            Text(text = if (locale == "ko") "취소" else "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                inputRateText.toDoubleOrNull()?.let {
                                    viewModel.updateExchangeRate(it)
                                    editRateShow = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = if (locale == "ko") "적용" else "Apply", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. LOGBOOK SCREEN
// ==========================================
@Composable
fun LogbookScreen(viewModel: DiverViewModel, locale: String) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    // Forms Inputs States
    var placeInput by remember { mutableStateOf("") }
    var depthInput by remember { mutableStateOf("") }
    var minInput by remember { mutableStateOf("") }
    var visInput by remember { mutableStateOf("15m Clear") }
    var tempInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var ratingInput by remember { mutableStateOf(5) }
    var tagBuddyRecruit by remember { mutableStateOf(false) }

    // Simulated local photos to attach (strictly limited to 3 maximum: "사진을 업로드시 3개 제한")
    var selectedPhotosList = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = LocalizedStrings.get("nav_log", locale),
                    color = AquaCyanNeon,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    text = if (locale == "ko") "오프라인 한도 기록 및 동기화 지원" else "Supports offline logging & safety lock",
                    color = MistyGreyText,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    selectedPhotosList.clear()
                    placeInput = ""
                    depthInput = ""
                    minInput = ""
                    tempInput = ""
                    notesInput = ""
                    ratingInput = 5
                    tagBuddyRecruit = false
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_log_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Log", tint = OceanBlueAbyss)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = LocalizedStrings.get("add_log", locale), color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "No items list",
                        modifier = Modifier.size(60.dp),
                        tint = OceanTealDepth
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (locale == "ko") "기록된 다이빙 로그북이 없습니다.\n첫 기록을 터치해 등록하세요!"
                               else "No registered dive logs found.\nClick Add Log to register your first dive!",
                        color = MistyGreyText,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1.0f)
            ) {
                items(logs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, OceanTealDepth)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top row showing Date & Sync Status icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Date",
                                        tint = MarineTealBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = log.date, color = MistyGreyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                // Sync label
                                if (log.isSynced) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = "Synced label",
                                            tint = GlowGreenClean,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Synced", color = GlowGreenClean, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudQueue,
                                            contentDescription = "Offline Saved label",
                                            tint = CoralAlertRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Offline pending", color = CoralAlertRed, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Title row with spot location
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Spot icon", tint = AquaCyanNeon, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = log.location,
                                    color = DeepWhiteText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats section
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OceanTealDepth.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (locale == "ko") "수심" else "Depth", color = MistyGreyText, fontSize = 10.sp)
                                    Text(text = "${log.depthMeters}m", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (locale == "ko") "시간" else "Bottom Time", color = MistyGreyText, fontSize = 10.sp)
                                    Text(text = "${log.bottomTimeMinutes}m", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (locale == "ko") "수온" else "Temp", color = MistyGreyText, fontSize = 10.sp)
                                    Text(text = "${log.temperatureCelsius}°C", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (locale == "ko") "시야" else "Vis", color = MistyGreyText, fontSize = 10.sp)
                                    Text(text = log.visibility, color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Notes caption
                            Text(text = log.notes, color = DeepWhiteText, fontSize = 13.sp)

                            // Render photos strictly adhering to the 3 upload limit
                            val photos = if (log.photoUrlsJson.isEmpty() || log.photoUrlsJson == "[]") {
                                emptyList()
                            } else {
                                log.photoUrlsJson
                                    .replace("[", "")
                                    .replace("]", "")
                                    .split(",")
                                    .map { it.trim().replace("\"", "") }
                                    .filter { it.isNotEmpty() }
                            }

                            if (photos.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Attached Photos (${photos.size}/3):",
                                    color = MarineTealBright,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    photos.forEach { filename ->
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(OceanTealDepth),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Mock underwater colorful photo background
                                            Icon(
                                                imageVector = Icons.Default.CameraAlt,
                                                contentDescription = "Photo attached",
                                                tint = AquaCyanNeon.copy(alpha = 0.60f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Instructor Feedback Section
                            if (log.instructorFeedback.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = OceanTealDepth.copy(alpha = 0.3f)),
                                    border = BorderStroke(1.dp, MarineTealBright.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Forum,
                                            contentDescription = "Feedback icon",
                                            tint = AquaCyanNeon,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = if (locale == "ko") "강사 실시간 피드백" else "Instructor Feedback",
                                                color = AquaCyanNeon,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(text = log.instructorFeedback, color = DeepWhiteText, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Delete button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (log.isBuddyRecruitment) {
                                    AssistChip(
                                        onClick = {},
                                        colors = AssistChipDefaults.assistChipColors(labelColor = MarineTealBright),
                                        label = { Text("Recruiting Buddy", fontSize = 10.sp) },
                                        leadingIcon = { Icon(imageVector = Icons.Default.Group, contentDescription = "Active recruitment", modifier = Modifier.size(12.dp), tint = MarineTealBright) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                TextButton(
                                    onClick = { viewModel.deleteLog(log.id) },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CoralAlertRed.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (locale == "ko") "삭제" else "Delete", color = CoralAlertRed.copy(alpha = 0.8f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add logging dialog screen with 3 photo limit enforcement
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = LocalizedStrings.get("add_log", locale),
                        color = AquaCyanNeon,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Text fields
                    OutlinedTextField(
                        value = placeInput,
                        onValueChange = { placeInput = it },
                        label = { Text("Dive Spot Name", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AquaCyanNeon, focusedLabelColor = AquaCyanNeon,
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("log_spot_input")
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = depthInput,
                            onValueChange = { depthInput = it },
                            label = { Text("Depth (m)", color = MistyGreyText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp, bottom = 8.dp)
                                .testTag("log_depth_input")
                        )
                        OutlinedTextField(
                            value = minInput,
                            onValueChange = { minInput = it },
                            label = { Text("Time (Min)", color = MistyGreyText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp, bottom = 8.dp)
                                .testTag("log_time_input")
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = tempInput,
                            onValueChange = { tempInput = it },
                            label = { Text("Temp (°C)", color = MistyGreyText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp, bottom = 8.dp)
                                .testTag("log_temp_input")
                        )
                        OutlinedTextField(
                            value = visInput,
                            onValueChange = { visInput = it },
                            label = { Text("Visibility (eg. 15m)", color = MistyGreyText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp, bottom = 8.dp)
                                .testTag("log_vis_input")
                        )
                    }

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("General Notes & Sights", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(bottom = 12.dp)
                            .testTag("log_notes_input")
                    )

                    // Photo attaches with limit tag check strictly
                    Text(
                        text = "Upload Experience Photos (Max 3 Allowed):",
                        color = DeepWhiteText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (selectedPhotosList.size < 3) {
                                    selectedPhotosList.add("mock_photo_${selectedPhotosList.size + 1}")
                                }
                            },
                            enabled = selectedPhotosList.size < 3,
                            colors = ButtonDefaults.buttonColors(containerColor = OceanTealDepth)
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Attach photo")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Photo")
                        }

                        selectedPhotosList.forEach { photo ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MarineTealBright, RoundedCornerShape(4.dp))
                                    .clickable { selectedPhotosList.remove(photo) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "X", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                    if (selectedPhotosList.size >= 3) {
                        Text(
                            text = LocalizedStrings.get("photo_limit_msg", locale),
                            color = CoralAlertRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buddy tag recruit option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = tagBuddyRecruit,
                            onCheckedChange = { tagBuddyRecruit = it },
                            colors = CheckboxDefaults.colors(checkedColor = AquaCyanNeon)
                        )
                        Column {
                            Text(
                                text = "Recruit Buddy (\"버디 구함\")",
                                color = DeepWhiteText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Instantly post this to community recruitment boards too",
                                color = MistyGreyText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save / close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text(text = if (locale == "ko") "취소" else "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (placeInput.isNotBlank()) {
                                    viewModel.addDiveLog(
                                        location = placeInput,
                                        depth = depthInput.toDoubleOrNull() ?: 12.0,
                                        duration = minInput.toIntOrNull() ?: 40,
                                        visibility = visInput,
                                        temp = tempInput.toDoubleOrNull() ?: 24.0,
                                        notes = notesInput,
                                        rating = ratingInput,
                                        photos = selectedPhotosList.toList(),
                                        isBuddy = tagBuddyRecruit
                                    )
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = if (locale == "ko") "기록" else "Save Log", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. INSTRUCTOR & SAFETY SCREEN
// ==========================================
@Composable
fun InstructorAndSafetyScreen(viewModel: DiverViewModel, locale: String) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val gears by viewModel.gears.collectAsStateWithLifecycle()
    val gearChecklist by viewModel.gearChecklist.collectAsStateWithLifecycle()

    var showStudentRegDialog by remember { mutableStateOf(false) }
    var gearRegShow by remember { mutableStateOf(false) }

    // Forms Inputs States
    var studentNameInput by remember { mutableStateOf("") }
    var studentEmailInput by remember { mutableStateOf("") }
    var studentCertInput by remember { mutableStateOf("SSI Open Water Diver") }

    var gearNameInput by remember { mutableStateOf("") }
    var gearCategoryInput by remember { mutableStateOf("Computer") }
    var gearPriceInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section - Instructor Dashboard
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = LocalizedStrings.get("student_cert", locale),
                        color = AquaCyanNeon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (locale == "ko") "교육생 진행도 시각적 그래프 및 피드백 전송" else "Student progression tracking dashboard",
                        color = MistyGreyText,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showStudentRegDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MarineTealBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("reg_student_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Student", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Add Student", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        if (students.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue)) {
                    Text(
                        text = "No students enrolled yet.",
                        color = MistyGreyText,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(students) { student ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, OceanTealDepth)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = student.studentName, color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = student.studentEmail, color = MistyGreyText, fontSize = 11.sp)
                            }
                            
                            // Visual status label
                            AssistChip(
                                onClick = {},
                                shape = RoundedCornerShape(8.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = OceanTealDepth,
                                    labelColor = if (student.status == "Issued") GlowGreenClean else AquaCyanNeon
                                ),
                                label = { Text(text = student.status, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress indicator dashboard ("교육 진행 상황을 시각적으로 확인하는 대시보드")
                        Text(
                            text = "${LocalizedStrings.get("reviews", locale)}: Completed ${student.progressPercent}% Skill Modules",
                            color = DeepWhiteText,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { student.progressPercent / 100f },
                                modifier = Modifier
                                    .weight(1.0f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (student.progressPercent == 100) GlowGreenClean else AquaCyanNeon,
                                trackColor = OceanTealDepth
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "${student.progressPercent}%", color = AquaCyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Shared Training Logs ("교육 일지 공유 기능")
                        if (student.trainingLogSharing.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OceanTealDepth.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = "Logs",
                                    tint = MarineTealBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (locale == "ko") "공유 교육 일지" else "Shared Lecture Logs",
                                        color = MarineTealBright,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = student.trainingLogSharing, color = DeepWhiteText, fontSize = 11.sp)
                                }
                            }
                        }

                        // Feedbacks from Instructor ("강사와 학생 간의 실시간 피드백 시스템")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(OceanTealDepth.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubble,
                                contentDescription = "Instructor feedback",
                                tint = GlowGreenClean,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (locale == "ko") "실시간 피드백 및 평가" else "Instructor Assessment Note",
                                    color = GlowGreenClean,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = student.feedbackFromInstructor, color = DeepWhiteText, fontSize = 11.sp)
                            }
                        }

                        // Actions - interactive review/progress updates
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    // Simulate progressive modular updates
                                    val nextProg = (student.progressPercent + 25).coerceAtMost(100)
                                    val newStatus = if (nextProg == 100) "Issued" else "In Progress"
                                    val sharedLog = "Module $nextProg Ocean dives completed successfully."
                                    val newFeed = "Buoyancy is good. Ready for Deep Specialty next!"
                                    viewModel.updateStudentProgress(
                                        student.id,
                                        student.certificateName,
                                        nextProg,
                                        newStatus,
                                        newFeed,
                                        sharedLog
                                    )
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Validate module", tint = AquaCyanNeon, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = if (locale == "ko") "진행인증" else "Approve Progress", color = AquaCyanNeon, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = { viewModel.deleteStudent(student.id) }
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete student", tint = CoralAlertRed.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = if (locale == "ko") "해지" else "Remove", color = CoralAlertRed.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section - Gear Checklist
        item {
            Column {
                Text(
                    text = LocalizedStrings.get("gear_check", locale),
                    color = AquaCyanNeon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = if (locale == "ko") "이벤트 전 수중 안전 필수 점검 매뉴얼" else "Pre-dive physical safe equipment check-offs",
                    color = MistyGreyText,
                    fontSize = 11.sp
                )
            }
        }

        items(gearChecklist.size) { index ->
            val (item, isChecked) = gearChecklist[index]
            Surface(
                color = OceanSurfaceBlue,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, OceanTealDepth),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleChecklistItem(index) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { viewModel.toggleChecklistItem(index) },
                        colors = CheckboxDefaults.colors(checkedColor = AquaCyanNeon)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item,
                        color = if (isChecked) MistyGreyText else DeepWhiteText,
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        ),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Section - Purchase history and inventory ("개인별 장비 관리와 점검 주기 알림")
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = LocalizedStrings.get("gear_purchase", locale),
                        color = AquaCyanNeon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (locale == "ko") "장비별 점검 정보 및 환율 변환 가격 표시" else "Personal gear maintenance schedules & price exchange",
                        color = MistyGreyText,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { gearRegShow = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MarineTealBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_gear_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Gear", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Add Gear", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        if (gears.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue)) {
                    Text(text = "No personal gears listed.", color = MistyGreyText, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(gears) { gear ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, OceanTealDepth)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (gear.category == "Computer") Icons.Default.Watch else Icons.Default.Handyman,
                                    contentDescription = "Gear Category",
                                    tint = AquaCyanNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = gear.name, color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Category: ${gear.category}", color = MistyGreyText, fontSize = 11.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                // Multi-currency displays ("화폐 는 한국 화폐와 달라. 환율도 표시")
                                Text(text = "$${gear.purchasePriceUsd} USD", color = DeepWhiteText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = viewModel.formatCurrency(gear.purchasePriceUsd),
                                    color = AquaCyanNeon,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Maintenance Schedule Warning Alert ("점검 주기 알림")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(OceanTealDepth.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (locale == "ko") "마지막 정밀 점검: ${gear.lastCheckDate}" else "Last Service: ${gear.lastCheckDate}",
                                    color = MistyGreyText,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (locale == "ko") "점검 예정 주기: ${gear.nextCheckDate}" else "Next Inspection: ${gear.nextCheckDate}",
                                    color = if (gear.status == "Needs Service") CoralAlertRed else GlowGreenClean,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // If next service needed
                            if (gear.status == "Needs Service") {
                                Box(
                                    modifier = Modifier
                                        .background(CoralAlertRed, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "OVERDUE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            } else {
                                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Safe Seal", tint = GlowGreenClean, modifier = Modifier.size(20.dp))
                            }
                        }

                        // Gear Action
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Status: ${gear.status}", color = MistyGreyText, fontSize = 11.sp)
                            Row {
                                TextButton(onClick = { viewModel.checkUpGear(gear.id) }) {
                                    Icon(imageVector = Icons.Default.Build, contentDescription = "Service tool", tint = AquaCyanNeon, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = if (locale == "ko") "자가점검완료" else "Inspect Done", color = AquaCyanNeon, fontSize = 11.sp)
                                }
                                TextButton(onClick = { viewModel.deleteGear(gear.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete gear", tint = CoralAlertRed.copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = if (locale == "ko") "해제" else "Remove", color = CoralAlertRed.copy(alpha = 0.7f), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Emergency Safety Guidelines manual card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OceanTealDepth)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = "Safety Manual", tint = CoralAlertRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LocalizedStrings.get("emergency_manual", locale),
                                color = DeepWhiteText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand info",
                            tint = DeepWhiteText
                        )
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (locale == "ko") {
                                "🚨 1. 고압 산소 결핍 및 중도 차단 대비:\n" +
                                        "  - 동료 세컨더리 호흡기를 수색 후 결합하십시오.\n" +
                                        "🚨 2. 급속 상승 방지 (감압병 예방):\n" +
                                        "  - 5m 깊이에서 최소 3분간 감압 안전 정지를 진행하세요.\n" +
                                        "🚨 3. 수중 조류 난조 시 대처:\n" +
                                        "  - 바위 바닥을 잡고 엎드려 자세를 낮춘 뒤 동료에게 구조 수신호를 전송하세요."
                            } else {
                                "🚨 1. Out of Air emergency:\n" +
                                        "  - Secure buddy alternate air source immediately. Signal out-of-air!\n" +
                                        "🚨 2. Safe ascent speed limits:\n" +
                                        "  - Never exceed 9m/minute. Complete 3-minute safety stop at 5m deep.\n" +
                                        "🚨 3. Entrapment or currents:\n" +
                                        "  - Stay calm, hold onto substrate, deploy SMB safety tube to alert surface boat crew."
                            },
                            color = DeepWhiteText,
                            fontSize = 12.sp,
                            style = androidx.compose.ui.text.TextStyle(lineHeight = 18.sp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Modal dialogs for Student addition
    if (showStudentRegDialog) {
        Dialog(onDismissRequest = { showStudentRegDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Register Training Student",
                        color = AquaCyanNeon,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = studentNameInput,
                        onValueChange = { studentNameInput = it },
                        label = { Text("Student Name", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("student_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = studentEmailInput,
                        onValueChange = { studentEmailInput = it },
                        label = { Text("E-mail address", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("student_email_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = studentCertInput,
                        onValueChange = { studentCertInput = it },
                        label = { Text("Certification Track", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("student_cert_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showStudentRegDialog = false }) {
                            Text(text = "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (studentNameInput.isNotBlank()) {
                                    viewModel.addStudent(
                                        studentNameInput,
                                        studentEmailInput,
                                        studentCertInput
                                    )
                                    showStudentRegDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = "Register", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue of gear registration
    if (gearRegShow) {
        Dialog(onDismissRequest = { gearRegShow = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Register Dive Gear Inventory",
                        color = AquaCyanNeon,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = gearNameInput,
                        onValueChange = { gearNameInput = it },
                        label = { Text("Gear Model Name", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("gear_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gearCategoryInput,
                        onValueChange = { gearCategoryInput = it },
                        label = { Text("Category (BCD, Computer, Regulator...)", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("gear_cat_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gearPriceInput,
                        onValueChange = { gearPriceInput = it },
                        label = { Text("Purchase Price (USD)", color = MistyGreyText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("gear_price_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { gearRegShow = false }) {
                            Text(text = "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (gearNameInput.isNotBlank()) {
                                    val usdVal = gearPriceInput.toDoubleOrNull() ?: 150.0
                                    viewModel.addGear(gearNameInput, gearCategoryInput, usdVal)
                                    gearRegShow = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = "Save", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. TOURS & BOOKING SCREEN
// ==========================================
@Composable
fun ToursAndBookingScreen(viewModel: DiverViewModel, locale: String) {
    val tours by viewModel.tours.collectAsStateWithLifecycle()
    val buddyPosts by viewModel.buddyPosts.collectAsStateWithLifecycle()
    val usdToKrwRate by viewModel.usdToKrwRate.collectAsStateWithLifecycle()

    var showReceiptDialog by remember { mutableStateOf<DiveTour?>(null) }
    var paySimulationTour by remember { mutableStateOf<DiveTour?>(null) }
    var reviewTourModal by remember { mutableStateOf<DiveTour?>(null) }

    // Forms Inputs for Booking
    var cardNumInput by remember { mutableStateOf("") }
    var cardPinInput by remember { mutableStateOf("") }

    // Review inputs
    var reviewRatingInput by remember { mutableStateOf(5) }
    var reviewTextInput by remember { mutableStateOf("") }

    // Forms for Buddy hunt posting
    var showBuddyPostAdd by remember { mutableStateOf(false) }
    var buddyTitle by remember { mutableStateOf("") }
    var buddyDest by remember { mutableStateOf("") }
    var buddyMax by remember { mutableStateOf("2") }
    var buddyContact by remember { mutableStateOf("") }
    var buddyNotes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Buddy recruitment ("버디 구함") Board section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = LocalizedStrings.get("buddy_hunt", locale),
                        color = AquaCyanNeon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (locale == "ko") "함께 다이빙할 안전 동료 버디 구함!" else "Meet and recruit companions for safety",
                        color = MistyGreyText,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showBuddyPostAdd = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MarineTealBright),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("buddy_rec_post_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Buddy Hunt", tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Find Buddy", color = Color.White, fontSize = 11.sp)
                }
            }
        }

        if (buddyPosts.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue)) {
                    Text(text = "No buddy hunts posted.", color = MistyGreyText, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(buddyPosts) { post ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, OceanTealDepth)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = post.title, color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = "User", tint = MarineTealBright, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.diverName} (${post.diverLevel})", color = MistyGreyText, fontSize = 11.sp)
                                }
                            }
                            
                            // Buddies quota indicators
                            Box(
                                modifier = Modifier
                                    .background(OceanTealDepth, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Buddies: ${post.currentBuddies}/${post.maxBuddies}",
                                    color = AquaCyanNeon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Destination: ${post.destination} | Date: ${post.date}", color = AquaCyanNeon, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = post.notes, color = DeepWhiteText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Contact tags
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Contact: ${post.contactInfo}", color = MarineTealBright, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            
                            Row {
                                Button(
                                    onClick = { viewModel.joinBuddyPost(post.id) },
                                    enabled = post.currentBuddies < post.maxBuddies,
                                    colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon),
                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(text = if (locale == "ko") "참가신청" else "Join Buddy", color = OceanBlueAbyss, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { viewModel.deleteBuddyPost(post.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete post", tint = CoralAlertRed.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section - Dive Tours Booking and Bill list
        item {
            Column {
                Text(
                    text = LocalizedStrings.get("tour_book", locale),
                    color = AquaCyanNeon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = if (locale == "ko") "다이빙 투어 예약 및 간편 결제, 예산 관리 시트" else "Manage tour schedules, bookings, budgets, and reviews",
                    color = MistyGreyText,
                    fontSize = 11.sp
                )
            }
        }

        items(tours) { tour ->
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, OceanTealDepth)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = tour.title, color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        
                        // Booked status badge
                        if (tour.isBooked) {
                            Box(
                                modifier = Modifier
                                    .background(GlowGreenClean, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "BOOKED PAID", color = OceanBlueAbyss, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(OceanTealDepth, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "AVAILABLE", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Location: ${tour.destination} | Date: ${tour.date}", color = MarineTealBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = tour.description, color = DeepWhiteText, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Localized Multi-currency pricing row ("화폐는 한국 화폐와 달라. 환율도 표시")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OceanTealDepth.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Cost per Diver ($):", color = MistyGreyText, fontSize = 11.sp)
                            Text(text = "$${tour.costUsd} USD", color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "arrow", tint = AquaCyanNeon)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Korean Converted Won (₩):", color = MistyGreyText, fontSize = 10.sp)
                            Text(
                                text = String.format("₩%,.0f원", tour.costUsd * usdToKrwRate),
                                color = AquaCyanNeon,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Participant roster shared gallery lists ("투어 참가자들끼리 사진을 공유")
                    val participants = if (tour.participantsJson.isEmpty() || tour.participantsJson == "[]") {
                        emptyList()
                    } else {
                        tour.participantsJson
                            .replace("[", "")
                            .replace("]", "")
                            .split(",")
                            .map { it.trim().replace("\"", "") }
                            .filter { it.isNotEmpty() }
                    }

                    if (participants.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Roster (${participants.size}/${tour.maxParticipants}): " + participants.joinToString(", "),
                            color = MistyGreyText,
                            fontSize = 11.sp
                        )
                    }

                    // Shared Photos Gallery specifically for this tour ("참가자들끼리 사진을 공유할 수 있는 갤러리 기능")
                    val tourPhotos = if (tour.photosJson.isEmpty() || tour.photosJson == "[]") {
                        emptyList()
                    } else {
                        tour.photosJson
                            .replace("[", "")
                            .replace("]", "")
                            .split(",")
                            .map { it.trim().replace("\"", "") }
                            .filter { it.isNotEmpty() }
                    }

                    if (tourPhotos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Shared Tour Member Gallery (${tourPhotos.size}/3 limits):", color = MarineTealBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            tourPhotos.forEach { photo ->
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(OceanTealDepth),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Image, contentDescription = "Tour pic", tint = AquaCyanNeon.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    // Tour Budget & Finance tracker tool ("투어 예산과 지출 관리하는 비용 정산 도구")
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OceanTealDepth.copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, OceanTealDepth)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = LocalizedStrings.get("budget_exp", locale), color = DeepWhiteText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(
                                    text = "Rate: $1 = ${usdToKrwRate}원",
                                    color = MistyGreyText,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Target Budget:", color = MistyGreyText, fontSize = 10.sp)
                                    Text(text = "$${tour.budgetUsd} (${viewModel.formatCurrency(tour.budgetUsd)})", color = DeepWhiteText, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text(text = "Actual Spent:", color = MistyGreyText, fontSize = 10.sp)
                                    Text(text = "$${tour.expensesUsd} (${viewModel.formatCurrency(tour.expensesUsd)})", color = if (tour.expensesUsd > tour.budgetUsd) CoralAlertRed else GlowGreenClean, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            // Visual balance bar
                            Spacer(modifier = Modifier.height(6.dp))
                            val ratio = if (tour.budgetUsd > 0) (tour.expensesUsd / tour.budgetUsd).toFloat().coerceIn(0f..1f) else 0f
                            LinearProgressIndicator(
                                progress = { ratio },
                                color = if (tour.expensesUsd > tour.budgetUsd) CoralAlertRed else MarineTealBright,
                                trackColor = OceanTealDepth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )
                        }
                    }

                    // Review stars evaluating dive system ("다이빙 포인트별 리뷰와 별점 평가 시스템")
                    if (tour.reviewText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(OceanTealDepth.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Member Review: ", color = AquaCyanNeon, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    repeat(tour.reviewRating) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = MarineGoldAccent, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Text(text = "\"${tour.reviewText}\"", color = DeepWhiteText, fontSize = 11.sp, style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                            }
                        }
                    }

                    // Actions Row - Booking / Cancel / Leave review Rating
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (tour.isBooked) {
                            Button(
                                onClick = { reviewTourModal = tour },
                                colors = ButtonDefaults.buttonColors(containerColor = OceanTealDepth),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.RateReview, contentDescription = "Rating post", tint = AquaCyanNeon, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Write Review", color = AquaCyanNeon, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { viewModel.cancelTour(tour.id) }) {
                                Text(text = if (locale == "ko") "예약취소" else "Cancel Book", color = CoralAlertRed, fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = { paySimulationTour = tour },
                                colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(35.dp)
                                    .testTag("book_tour_${tour.id}")
                            ) {
                                Icon(imageVector = Icons.Default.CreditCard, contentDescription = "Simulate payment card", tint = OceanBlueAbyss, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (locale == "ko") "결제예약" else "Book & Pay", color = OceanBlueAbyss, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { showReceiptDialog = tour }) {
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Billing receipt", tint = MarineTealBright)
                        }
                    }
                }
            }
        }
    }

    // Modal Receipt/Budget Breakdown
    showReceiptDialog?.let { tour ->
        Dialog(onDismissRequest = { showReceiptDialog = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Receipt & Financials Summary", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        IconButton(onClick = { showReceiptDialog = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "close", tint = DeepWhiteText)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Tour Item: ${tour.title}", color = DeepWhiteText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Target Destination: ${tour.destination}", color = MistyGreyText, fontSize = 12.sp)
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = OceanTealDepth)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Registration Fee ($):", color = MistyGreyText, fontSize = 12.sp)
                        Text(text = "$${tour.costUsd} USD", color = DeepWhiteText, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "In Won (₩ at rate):", color = MistyGreyText, fontSize = 12.sp)
                        Text(text = String.format("₩%,.0f원", tour.costUsd * usdToKrwRate), color = AquaCyanNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = OceanTealDepth)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Local Tour Budget Sheet (Korean Exchange):", color = MarineTealBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Total Authorized Budget: " + viewModel.formatCurrency(tour.budgetUsd), color = DeepWhiteText, fontSize = 12.sp)
                    Text(text = "Current Real Expenditures: " + viewModel.formatCurrency(tour.expensesUsd), color = DeepWhiteText, fontSize = 12.sp)
                    Text(
                        text = "Margin Remaining: " + viewModel.formatCurrency(tour.budgetUsd - tour.expensesUsd),
                        color = if (tour.expensesUsd > tour.budgetUsd) CoralAlertRed else GlowGreenClean,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Interactive simulated instant payment sheet ("결제와 예약 확정 기능을 위한 인터페이스")
    paySimulationTour?.let { tour ->
        Dialog(onDismissRequest = { paySimulationTour = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = LocalizedStrings.get("pay_title", locale),
                        color = AquaCyanNeon,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(text = "Item: ${tour.title}", color = DeepWhiteText, fontSize = 13.sp)
                    Text(text = "Price to Charge: ${viewModel.formatCurrency(tour.costUsd)} ($${tour.costUsd} USD)", color = AquaCyanNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cardNumInput,
                        onValueChange = { cardNumInput = it },
                        label = { Text("Credit Card Number (16 Digits)", color = MistyGreyText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("payment_card_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cardPinInput,
                        onValueChange = { cardPinInput = it },
                        label = { Text("Card PIN Password", color = MistyGreyText) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("payment_pin_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { paySimulationTour = null }) {
                            Text(text = "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (cardNumInput.length >= 4) {
                                    viewModel.bookTour(tour.id)
                                    paySimulationTour = null
                                    cardNumInput = ""
                                    cardPinInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = "Simulate Pay Now", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Rate tour model
    reviewTourModal?.let { tour ->
        Dialog(onDismissRequest = { reviewTourModal = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Submit Dive Point Review",
                        color = AquaCyanNeon,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { starIndex ->
                            val selected = starIndex < reviewRatingInput
                            IconButton(onClick = { reviewRatingInput = starIndex + 1 }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating star",
                                    tint = if (selected) MarineGoldAccent else MistyGreyText,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewTextInput,
                        onValueChange = { reviewTextInput = it },
                        label = { Text("Write experience evaluation details...", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { reviewTourModal = null }) {
                            Text(text = "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.rateTour(tour.id, reviewTextInput, reviewRatingInput)
                                reviewTourModal = null
                                reviewTextInput = ""
                                reviewRatingInput = 5
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = "Submit Review", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog for posting buddy recruitment
    if (showBuddyPostAdd) {
        Dialog(onDismissRequest = { showBuddyPostAdd = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AquaCyanNeon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = LocalizedStrings.get("add_buddy", locale),
                        color = AquaCyanNeon,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = buddyTitle,
                        onValueChange = { buddyTitle = it },
                        label = { Text("Post Title (e.g., Night dive!)", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("buddy_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = buddyDest,
                        onValueChange = { buddyDest = it },
                        label = { Text("Destination/Shop Location", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("buddy_dest_input")
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = buddyMax,
                            onValueChange = { buddyMax = it },
                            label = { Text("Required Count", color = MistyGreyText) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        )
                        OutlinedTextField(
                            value = buddyContact,
                            onValueChange = { buddyContact = it },
                            label = { Text("Kakao ID / WhatsApp", color = MistyGreyText) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                            ),
                            modifier = Modifier
                                .weight(1.5f)
                                .padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = buddyNotes,
                        onValueChange = { buddyNotes = it },
                        label = { Text("Brief notes (level, gear requirements)", color = MistyGreyText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showBuddyPostAdd = false }) {
                            Text(text = "Cancel", color = MistyGreyText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (buddyTitle.isNotBlank()) {
                                    val count = buddyMax.toIntOrNull() ?: 2
                                    viewModel.addBuddyPost(buddyTitle, buddyDest, count, buddyContact, buddyNotes)
                                    showBuddyPostAdd = false
                                    buddyTitle = ""
                                    buddyDest = ""
                                    buddyContact = ""
                                    buddyNotes = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AquaCyanNeon)
                        ) {
                            Text(text = "Post Now", color = OceanBlueAbyss, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. COMMUNITY & CHAT SCREEN
// ==========================================
@Composable
fun CommunityAndChatScreen(viewModel: DiverViewModel, locale: String) {
    val groupChats by viewModel.groupChats.collectAsStateWithLifecycle()
    val instructorChats by viewModel.instructorChats.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Tour Group Chat, 1: Instructor Feedback Private Chat

    var chatTextInput by remember { mutableStateOf("") }
    val listState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Community board switcher tabs to separate cleanly
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = OceanSurfaceBlue,
            contentColor = AquaCyanNeon,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, OceanTealDepth, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text(text = LocalizedStrings.get("group_chat", locale), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = AquaCyanNeon,
                unselectedContentColor = MistyGreyText
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text(text = LocalizedStrings.get("inst_chat", locale), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                selectedContentColor = AquaCyanNeon,
                unselectedContentColor = MistyGreyText
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chat Box area
        val activeChats = if (activeSubTab == 0) groupChats else instructorChats

        Card(
            colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, OceanTealDepth),
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Chat header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OceanTealDepth.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (activeSubTab == 0) Icons.Default.Public else Icons.Default.Portrait,
                        contentDescription = "Active channel",
                        tint = AquaCyanNeon,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (activeSubTab == 0) "Jeju Expeditions Group Channel" else "Private Instructor Line",
                        color = DeepWhiteText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Green active indicator
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GlowGreenClean)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "LIVE", color = GlowGreenClean, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable messages list
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .verticalScroll(listState)
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activeChats.forEach { msg ->
                        val isMe = msg.isMe
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            // Sender nickname
                            Text(
                                text = if (isMe) "You (Student)" else msg.sender,
                                color = if (msg.isInstructor) AquaCyanNeon else MistyGreyText,
                                fontSize = 10.sp,
                                fontWeight = if (msg.isInstructor) FontWeight.Bold else FontWeight.Normal
                            )
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            // Bubble
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isMe) 12.dp else 0.dp,
                                            bottomEnd = if (isMe) 0.dp else 12.dp
                                        )
                                    )
                                    .background(
                                        if (isMe) AquaCyanNeon else OceanTealDepth
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (isMe) OceanBlueAbyss else DeepWhiteText,
                                    fontSize = 13.sp
                                )
                            }
                            
                            Text(text = msg.time, color = MistyGreyText, fontSize = 8.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Inputs for chats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatTextInput,
                        onValueChange = { chatTextInput = it },
                        placeholder = { Text("Type message securely offline...", color = MistyGreyText, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepWhiteText, unfocusedTextColor = DeepWhiteText,
                            focusedBorderColor = AquaCyanNeon, unfocusedBorderColor = OceanTealDepth
                        ),
                        modifier = Modifier
                            .weight(1.0f)
                            .height(52.dp)
                            .testTag("chat_input_text")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (chatTextInput.isNotBlank()) {
                                if (activeSubTab == 0) {
                                    viewModel.sendGroupChatMessage(chatTextInput)
                                } else {
                                    viewModel.sendInstructorChatMessage(chatTextInput)
                                }
                                chatTextInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AquaCyanNeon)
                            .testTag("send_chat_icon_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send secure", tint = OceanBlueAbyss)
                    }
                }
            }
        }
    }
}

// ==========================================
// EMERGENCY SOS OVERLAY WITH SIREN SOUND EFFECT SYMBOL
// ==========================================
@Composable
fun SosEmergencyOverlay(locale: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.90f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Glowing siren indicator pulsing
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(CoralAlertRed)
                    .drawBehind {
                        drawCircle(color = CoralAlertRed.copy(alpha = 0.4f), radius = size.minDimension * 0.8f)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "Distress strobe",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RED ALERT: SOS EMERGENCY CALL",
                color = CoralAlertRed,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = LocalizedStrings.get("sos_msg", locale),
                color = DeepWhiteText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mock coordinates transmission status
            Card(
                colors = CardDefaults.cardColors(containerColor = OceanSurfaceBlue),
                border = BorderStroke(1.dp, OceanTealDepth)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Mock Location GPS Detected:", color = MistyGreyText, fontSize = 11.sp)
                    Text(text = "Lat: 33.2201 | Lng: 126.5623", color = AquaCyanNeon, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "📡 Beacons sending on frequency 406 MHz...", color = MarineTealBright, fontSize = 11.sp)
                    Text(text = "🚨 Coast Guard & Maritime Patrol updated.", color = GlowGreenClean, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OceanTealDepth),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (locale == "ko") "구조 완료 또는 신호취소" else "Signal Cancelled / Safe",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
