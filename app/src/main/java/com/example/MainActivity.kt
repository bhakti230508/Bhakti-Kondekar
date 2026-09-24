package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChemicalProduct
import com.example.ui.components.*
import com.example.ui.theme.FromchemBackground
import com.example.ui.theme.FromchemPrimary
import com.example.ui.theme.FromchemTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FromchemTheme {
                FromchemApp()
            }
        }
    }
}

@Composable
fun FromchemApp() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Screen width determination & Desktop toggle
    val isTabletOrDesktop = configuration.screenWidthDp >= 600
    var isDesktopViewOverride by remember { mutableStateOf(isTabletOrDesktop) }

    var selectedTab by remember { mutableStateOf("Services") }
    var showQuoteModal by remember { mutableStateOf(false) }
    var showChatModal by remember { mutableStateOf(false) }
    var showLeakDetectorModal by remember { mutableStateOf(false) }
    var showContactModal by remember { mutableStateOf(false) }
    var showLoginModal by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<UserProfile?>(null) }
    var selectedCardForLearnMore by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = FromchemBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showChatModal = true },
                containerColor = FromchemPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("floating_ai_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice & AI Support Chat",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Voice & AI Chat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(FromchemBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Top Header Navigation Bar
                HeaderNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        when (tab) {
                            "Contact" -> {
                                showContactModal = true
                            }
                            "Products" -> {
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(360)
                                }
                                Toast.makeText(context, "Navigated to Products Catalog", Toast.LENGTH_SHORT).show()
                            }
                            "Projects" -> {
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(1400)
                                }
                                Toast.makeText(context, "Navigated to Project Gallery", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, "Navigated to $tab", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onGetQuoteClicked = { showQuoteModal = true },
                    isDesktopMode = isDesktopViewOverride,
                    onToggleDesktopMode = {
                        isDesktopViewOverride = !isDesktopViewOverride
                        val modeName = if (isDesktopViewOverride) "Desktop Web Layout" else "Android Mobile Layout"
                        Toast.makeText(context, "Switched to $modeName", Toast.LENGTH_SHORT).show()
                    },
                    currentUser = currentUser,
                    onAccountClicked = {
                        if (currentUser == null) {
                            showLoginModal = true
                        } else {
                            showProfileModal = true
                        }
                    },
                    onScanLeakClicked = { showLeakDetectorModal = true }
                )

                // Main Page Width Container (Centered with max width on ultra-wide screens)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .widthIn(max = 1200.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Hero Section
                        HeroSection(
                            onScanLeakClicked = { showLeakDetectorModal = true },
                            isWideScreen = isDesktopViewOverride
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Chemical Waterproofing Products & TDS Specification Catalog
                        ChemicalProductCatalogSection(
                            onRequestQuoteForProduct = { product ->
                                showQuoteModal = true
                                Toast.makeText(
                                    context,
                                    "Requesting Quote for ${product.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            isWideScreen = isDesktopViewOverride
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Our Expertise Section
                        OurExpertiseSection(
                            onCardClicked = { card ->
                                selectedCardForLearnMore = card.title
                            },
                            onChatClicked = {
                                showChatModal = true
                            },
                            isWideScreen = isDesktopViewOverride
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Project Photo Gallery Section
                        ProjectGallerySection(
                            onGetQuoteForProject = { projectTitle ->
                                showQuoteModal = true
                                Toast.makeText(context, "Requesting Quote for $projectTitle", Toast.LENGTH_SHORT).show()
                            },
                            isWideScreen = isDesktopViewOverride
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Why Chemical Waterproofing is the Future Section
                        WhyFutureSection(
                            onFeatureClicked = { featureName ->
                                Toast.makeText(context, "$featureName: ISO Certified Chemical Compound", Toast.LENGTH_SHORT).show()
                            },
                            isWideScreen = isDesktopViewOverride
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. Contact Us Inquiry Form Section
                        ContactUsSection(
                            isWideScreen = isDesktopViewOverride,
                            onInquirySubmitted = { form, id ->
                                Toast.makeText(context, "Inquiry #$id received from ${form.fullName}", Toast.LENGTH_LONG).show()
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 5. Footer Section
                        FooterSection(
                            onLinkClick = { link ->
                                if (link == "Contact") {
                                    showContactModal = true
                                } else {
                                    Toast.makeText(context, "Opening $link", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEmailClick = {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                        data = android.net.Uri.parse("mailto:fromchem6@gmail.com")
                                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Inquiry regarding Fromchem Waterproofing Services")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    showContactModal = true
                                    Toast.makeText(context, "Owner Email: fromchem6@gmail.com", Toast.LENGTH_LONG).show()
                                }
                            },
                            onPhoneClick = {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:+919727751868"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Fromchem Hotline: +91 97277 51868", Toast.LENGTH_LONG).show()
                                }
                            },
                            isWideScreen = isDesktopViewOverride
                        )
                    }
                }
            }

            // Interactive Modals & Dialogs
            if (showContactModal) {
                ContactUsDialog(
                    onDismiss = { showContactModal = false },
                    onInquirySubmitted = { form, id ->
                        Toast.makeText(context, "Inquiry #$id received from ${form.fullName}", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // Interactive Modals & Dialogs
            if (showQuoteModal) {
                GetQuoteDialog(
                    onDismiss = { showQuoteModal = false },
                    onSubmitQuote = { type, area, contact ->
                        Toast.makeText(context, "Quote request submitted for $type ($area sq ft)", Toast.LENGTH_LONG).show()
                    }
                )
            }

            if (showChatModal) {
                LiveChatDialog(
                    onDismiss = { showChatModal = false }
                )
            }

            if (showLeakDetectorModal) {
                AILeakDetectorDialog(
                    onDismiss = { showLeakDetectorModal = false },
                    onRequestQuoteForSolution = { solution, defectTitle ->
                        showQuoteModal = true
                        Toast.makeText(context, "Quote request initiated for $solution", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            selectedCardForLearnMore?.let { title ->
                LearnMoreDialog(
                    cardTitle = title,
                    onDismiss = { selectedCardForLearnMore = null }
                )
            }

            if (showLoginModal) {
                LoginDialog(
                    onDismiss = { showLoginModal = false },
                    onLoginSuccess = { user ->
                        currentUser = user
                        showLoginModal = false
                        Toast.makeText(context, "Welcome to Fromchem Portal, ${user.fullName}!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            if (showProfileModal && currentUser != null) {
                UserProfileDialog(
                    user = currentUser!!,
                    onDismiss = { showProfileModal = false },
                    onLogout = {
                        currentUser = null
                        showProfileModal = false
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
