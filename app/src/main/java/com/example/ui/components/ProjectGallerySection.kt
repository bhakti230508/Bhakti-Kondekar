package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.example.data.GalleryStorageManager
import java.util.UUID
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Authoritative Project data model for the Project Gallery.
 * Strictly adheres to the requirement that real, original images (user-uploaded or stored)
 * are displayed directly, prioritizing:
 * 1. User-uploaded original image (Uri / URL)
 * 2. Existing stored project image (Drawable resource)
 * 3. Clear "Image unavailable" placeholder
 * NEVER uses AI-generated replacement images.
 */
data class GalleryProject(
    val id: String,
    val title: String,
    val category: String,
    val location: String,
    val areaSize: String,
    val chemicalUsed: String,
    val durability: String,
    val description: String,
    val testResult: String,
    val photoCount: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    // Stored project drawable resources
    val beforeRes: Int? = null,
    val afterRes: Int? = null,
    val comparisonRes: Int? = null,
    // Real user-uploaded original images (exact original files/URIs)
    val userUploadedOriginalUris: List<Uri> = emptyList(),
    val userUploadedBeforeUri: Uri? = null,
    val userUploadedAfterUri: Uri? = null,
    val userUploadedComparisonUri: Uri? = null,
    val originalImageUrl: String? = null
)

val sampleGalleryProjects = listOf(
    GalleryProject(
        id = "proj_01",
        title = "Skyline IT Tower Rooftop",
        category = "Roof Waterproofing",
        location = "Ahmedabad, Gujarat",
        areaSize = "65,000 sq ft",
        chemicalUsed = "Fromchem Pure Polyurea 2000",
        durability = "25 Years Warranty",
        description = "Seamless elastomeric liquid spray coating applied directly over old concrete deck. 100% monolithic, jointless water barrier cured in 10 seconds.",
        testResult = "Hydrostatic Water Ponding Test Passed (72 Hours Zero Leak)",
        photoCount = 0,
        primaryColor = Color(0xFF0288D1),
        secondaryColor = Color(0xFF00ACC1),
        beforeRes = null,
        afterRes = null,
        comparisonRes = null
    ),
    GalleryProject(
        id = "proj_02",
        title = "Basement Retaining Wall Crack Injection & Tanking",
        category = "Basement Tanking",
        location = "Surat, Gujarat",
        areaSize = "1,20,000 sq ft",
        chemicalUsed = "Fromchem Crystalline Deep-Penetrant Slurry & PU Injection Grout",
        durability = "Lifetime Concrete Self-Healing",
        description = "High-pressure chemical polyurethane injection grouting and deep crystalline slurry applied directly to leaking basement retaining wall cracks under hydrostatic groundwater pressure.",
        testResult = "12 Bar Positive & Negative Hydrostatic Pressure Tested",
        photoCount = 0,
        primaryColor = Color(0xFF388E3C),
        secondaryColor = Color(0xFF66BB6A),
        beforeRes = null,
        afterRes = null,
        comparisonRes = null
    ),
    GalleryProject(
        id = "proj_03",
        title = "Grand Highway Viaduct Bridge Deck",
        category = "Bridge & Infrastructure",
        location = "Vadodara, Gujarat",
        areaSize = "85,000 sq ft",
        chemicalUsed = "Fromchem Hydro-Shield Epoxy Sealant",
        durability = "30 Years Heavy Traffic Endurance",
        description = "Heavy duty anti-corrosive chemical layer resistant to salts, de-icing chemicals, freeze-thaw cycles, and heavy vehicle vibration stress.",
        testResult = "ISO 9001 Crack Bridging Capacity 3.2mm Verified",
        photoCount = 0,
        primaryColor = Color(0xFFD32F2F),
        secondaryColor = Color(0xFFE53935),
        beforeRes = null,
        afterRes = null,
        comparisonRes = null
    ),
    GalleryProject(
        id = "proj_04",
        title = "Regal Commercial Plaza Podium Deck",
        category = "Podiums & Plazas",
        location = "Rajkot, Gujarat",
        areaSize = "42,000 sq ft",
        chemicalUsed = "Fromchem Elastomeric PU Membrane",
        durability = "20 Years Flex Coating",
        description = "UV-stable cold liquid applied polyurethane membrane designed for planter boxes, pedestrian plaza walkways, and structural expansion joints.",
        testResult = "400% Elongation at Break Certified",
        photoCount = 0,
        primaryColor = Color(0xFF7B1FA2),
        secondaryColor = Color(0xFFAB47BC),
        beforeRes = null,
        afterRes = null,
        comparisonRes = null
    ),
    GalleryProject(
        id = "proj_05",
        title = "Pharma Cleanroom Terrace Waterproofing",
        category = "Roof Waterproofing",
        location = "Bharuch, Gujarat",
        areaSize = "30,000 sq ft",
        chemicalUsed = "Fromchem Acrylic Elastomeric Reflective Coat",
        durability = "15 Years High Reflectance",
        description = "High SRI solar reflective thermal insulation coating combined with waterproof chemical polymers to reduce indoor temperature by up to 8°C.",
        testResult = "SRI Index 106 Certified (Energy Saving)",
        photoCount = 0,
        primaryColor = Color(0xFFF57C00),
        secondaryColor = Color(0xFFFFB74D),
        beforeRes = null,
        afterRes = null,
        comparisonRes = null
    ),
    GalleryProject(
        id = "proj_06",
        title = "Industrial Water Storage Tank Lining",
        category = "Basement Tanking",
        location = "Ankleshwar, Gujarat",
        areaSize = "25,000 sq ft",
        chemicalUsed = "Fromchem Food-Grade Potable Water Coating",
        durability = "20 Years Potable Safe",
        description = "Solvent-free epoxy chemical coating approved for drinking water reservoirs, chemical retention basins, and effluent treatment plants.",
        testResult = "BS 6920 Non-Toxic Drinking Water Certified",
        photoCount = 0,
        primaryColor = Color(0xFF00796B),
        secondaryColor = Color(0xFF26A69A),
        beforeRes = null,
        afterRes = null,
        comparisonRes = null
    )
)

/**
 * Standard Image Unavailable placeholder.
 * Displayed if original user image or stored image cannot be accessed.
 * Strictly avoids AI-generated or simulated images.
 */
@Composable
fun ImageUnavailablePlaceholder(
    modifier: Modifier = Modifier,
    label: String = "Image unavailable",
    subtitle: String = "No original project photo provided"
) {
    Box(
        modifier = modifier
            .background(Color(0xFF263238)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = label,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.55f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Direct Image Component implementing the mandatory priority order:
 * 1. User-uploaded original image (Uri / URL)
 * 2. Existing stored project image (Drawable resource)
 * 3. Clear "Image unavailable" placeholder
 * NEVER uses an AI-generated replacement.
 */
@Composable
fun AuthoritativeProjectImage(
    userUploadedUri: Uri? = null,
    userUploadedUrl: String? = null,
    storedResId: Int? = null,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    // Priority 1: User-uploaded original image (Uri or URL)
    if (userUploadedUri != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(userUploadedUri)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            error = {
                ImageUnavailablePlaceholder(
                    modifier = modifier,
                    label = "Image unavailable",
                    subtitle = "Uploaded image could not be loaded"
                )
            }
        )
    } else if (!userUploadedUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(userUploadedUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            error = {
                ImageUnavailablePlaceholder(
                    modifier = modifier,
                    label = "Image unavailable",
                    subtitle = "Original file unavailable"
                )
            }
        )
    } else if (storedResId != null) {
        // Priority 2: Existing stored project image
        Image(
            painter = painterResource(id = storedResId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        // Priority 3: Clear "Image unavailable" placeholder
        ImageUnavailablePlaceholder(
            modifier = modifier,
            label = "Image unavailable",
            subtitle = "No original project photo provided"
        )
    }
}

@Composable
fun ProjectGallerySection(
    onGetQuoteForProject: (String) -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var projectsList by remember {
        mutableStateOf(GalleryStorageManager.loadProjects(context))
    }
    val categories = listOf("All Projects", "Roof Waterproofing", "Basement Tanking", "Bridge & Infrastructure", "Podiums & Plazas")
    var selectedCategory by remember { mutableStateOf("All Projects") }

    val filteredProjects = remember(selectedCategory, projectsList) {
        if (selectedCategory == "All Projects") {
            projectsList
        } else {
            projectsList.filter { it.category == selectedCategory }
        }
    }

    val pagerState = rememberPagerState(pageCount = { maxOf(1, filteredProjects.size) })
    val coroutineScope = rememberCoroutineScope()

    var activeLightboxProject by remember { mutableStateOf<GalleryProject?>(null) }
    var uploadTargetProject by remember { mutableStateOf<GalleryProject?>(null) }
    var showUploadModal by remember { mutableStateOf(false) }
    var showAddProjectModal by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<GalleryProject?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("project_gallery_section")
    ) {
        // Section Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FromchemPrimaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Project Gallery",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REAL WORK & AUTHORITATIVE IMAGES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Text(
                text = "Waterproofing Project Gallery",
                fontSize = if (isWideScreen) 26.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                color = FromchemTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Verified original photos directly from real construction sites. Stored permanently on device.",
                fontSize = 13.sp,
                color = FromchemTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Primary Add to Gallery Button
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showAddProjectModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 9.dp),
                    modifier = Modifier.testTag("add_gallery_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add to Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add to Gallery",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (filteredProjects.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            uploadTargetProject = filteredProjects.getOrNull(pagerState.currentPage) ?: filteredProjects.firstOrNull()
                            showUploadModal = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
                        modifier = Modifier.testTag("upload_to_selected_project_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Upload Photos",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Upload Photos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips (visible if projects exist)
        if (projectsList.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = cat
                            coroutineScope.launch {
                                pagerState.scrollToPage(0)
                            }
                        },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FromchemPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = FromchemSurfaceVariant,
                            labelColor = FromchemTextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = FromchemBorder,
                            selectedBorderColor = FromchemPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (filteredProjects.isNotEmpty()) {
            // Gallery Swiper Pager
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 510.dp else 470.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = if (isWideScreen) 100.dp else 16.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    val project = filteredProjects.getOrNull(page)
                    if (project != null) {
                        GalleryProjectCard(
                            project = project,
                            onInspectClick = { activeLightboxProject = project },
                            onUploadPhotosClick = {
                                uploadTargetProject = project
                                showUploadModal = true
                            },
                            onDeleteProjectClick = {
                                projectToDelete = project
                            },
                            onGetQuoteClick = { onGetQuoteForProject(project.title) },
                            isWideScreen = isWideScreen
                        )
                    }
                }

                // Previous Button
                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.dp, FromchemBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Project",
                            tint = FromchemPrimary
                        )
                    }
                }

                // Next Button
                if (pagerState.currentPage < filteredProjects.size - 1) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.dp, FromchemBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Project",
                            tint = FromchemPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Page Indicator Dots & Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(filteredProjects.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width = if (isSelected) 24.dp else 8.dp
                        val color by animateColorAsState(
                            targetValue = if (isSelected) FromchemPrimary else FromchemBorder,
                            animationSpec = tween(300),
                            label = "dot_color"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                Text(
                    text = "Project ${pagerState.currentPage + 1} of ${filteredProjects.size} • Swipe horizontally 👈👉",
                    fontSize = 11.sp,
                    color = FromchemTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Empty State
            if (projectsList.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = FromchemSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Gallery Photos Yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FromchemTextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "All sample images have been cleared. Tap 'Add to Gallery' to upload your real project photos. Uploaded photos will remain stored permanently on your device even after closing the app.",
                            fontSize = 13.sp,
                            color = FromchemTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showAddProjectModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                            modifier = Modifier.testTag("add_gallery_empty_state_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add to Gallery",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Filter returned 0 results
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FromchemSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No projects found under \"$selectedCategory\"",
                            fontSize = 14.sp,
                            color = FromchemTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { selectedCategory = "All Projects" },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("View All Projects")
                            }
                            Button(
                                onClick = { showAddProjectModal = true },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                            ) {
                                Text("Add Project")
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Gallery Lightbox Modal
    activeLightboxProject?.let { project ->
        ProjectLightboxDialog(
            project = project,
            onDismiss = { activeLightboxProject = null },
            onGetQuoteClick = {
                activeLightboxProject = null
                onGetQuoteForProject(project.title)
            }
        )
    }

    // Direct User Upload Dialog (For an existing project)
    if (showUploadModal && uploadTargetProject != null) {
        UploadProjectPhotosDialog(
            project = uploadTargetProject!!,
            onDismiss = {
                showUploadModal = false
                uploadTargetProject = null
            },
            onPhotosUploaded = { updatedProject ->
                // Reload authoritative list from local disk
                projectsList = GalleryStorageManager.loadProjects(context)
                showUploadModal = false
                uploadTargetProject = null
            }
        )
    }

    // Add New Gallery Project / Photo Modal
    if (showAddProjectModal) {
        AddGalleryImageDialog(
            onDismiss = { showAddProjectModal = false },
            onProjectCreated = { createdProject ->
                // Reload authoritative list from local disk
                projectsList = GalleryStorageManager.loadProjects(context)
                showAddProjectModal = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Project", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently remove \"${projectToDelete?.title}\" and its photos from your gallery?") },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let {
                            projectsList = GalleryStorageManager.deleteProject(context, it.id)
                            Toast.makeText(context, "Project removed from gallery", Toast.LENGTH_SHORT).show()
                        }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Gallery Project Card displaying the authoritative project image.
 * Provides interactive Before / After / Compare toggles, directly rendering the real uploaded or stored image.
 */
@Composable
fun GalleryProjectCard(
    project: GalleryProject,
    onInspectClick: () -> Unit,
    onUploadPhotosClick: () -> Unit,
    onDeleteProjectClick: () -> Unit,
    onGetQuoteClick: () -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    // Initial tab logic
    val hasComparison = project.userUploadedComparisonUri != null || project.comparisonRes != null
    val hasBefore = project.userUploadedBeforeUri != null || project.beforeRes != null
    var selectedTab by remember(project.id) {
        mutableStateOf(if (hasComparison) "compare" else if (hasBefore) "before" else "after")
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, FromchemBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Visual Banner Graphic with Direct Authoritative Image Rendering
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 260.dp else 230.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                project.primaryColor.copy(alpha = 0.85f),
                                project.secondaryColor.copy(alpha = 0.95f)
                            )
                        )
                    )
            ) {
                // Direct rendering based on selected toggle (strictly no AI image generation)
                when (selectedTab) {
                    "before" -> {
                        AuthoritativeProjectImage(
                            userUploadedUri = project.userUploadedBeforeUri,
                            storedResId = project.beforeRes,
                            contentDescription = "Before: ${project.title}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "compare" -> {
                        AuthoritativeProjectImage(
                            userUploadedUri = project.userUploadedComparisonUri,
                            storedResId = project.comparisonRes ?: project.afterRes ?: project.beforeRes,
                            contentDescription = "Before and After: ${project.title}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        AuthoritativeProjectImage(
                            userUploadedUri = project.userUploadedAfterUri ?: project.userUploadedOriginalUris.firstOrNull(),
                            userUploadedUrl = project.originalImageUrl,
                            storedResId = project.afterRes ?: project.beforeRes,
                            contentDescription = "After: ${project.title}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Category & Durability Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = project.category,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Durability",
                                    tint = FromchemAccentGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = project.durability,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemTextPrimary
                                )
                            }
                        }

                        // Upload User Image Button
                        IconButton(
                            onClick = onUploadPhotosClick,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = "Upload original project photos",
                                tint = FromchemPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Fullscreen Zoom Icon Button
                        IconButton(
                            onClick = onInspectClick,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Expand,
                                contentDescription = "Fullscreen Photo Gallery",
                                tint = FromchemPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Delete Project Button
                        IconButton(
                            onClick = onDeleteProjectClick,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete project from gallery",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Interactive Before / After Toggle Buttons
                val canShowBefore = project.userUploadedBeforeUri != null || project.beforeRes != null
                val canShowAfter = project.userUploadedAfterUri != null || project.afterRes != null || project.userUploadedOriginalUris.isNotEmpty()
                val canShowCompare = project.userUploadedComparisonUri != null || project.comparisonRes != null

                if (canShowBefore || canShowCompare) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 4.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (canShowBefore) {
                            Surface(
                                onClick = { selectedTab = "before" },
                                shape = RoundedCornerShape(18.dp),
                                color = if (selectedTab == "before") Color.White else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Before",
                                        color = if (selectedTab == "before") Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == "before") FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (canShowAfter) {
                            Surface(
                                onClick = { selectedTab = "after" },
                                shape = RoundedCornerShape(18.dp),
                                color = if (selectedTab == "after") FromchemPrimary else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "After",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == "after") FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (canShowCompare) {
                            Surface(
                                onClick = { selectedTab = "compare" },
                                shape = RoundedCornerShape(18.dp),
                                color = if (selectedTab == "compare") Color(0xFF0D47A1) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Compare",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == "compare") FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Project Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = project.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = project.location,
                        fontSize = 12.sp,
                        color = FromchemTextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.SquareFoot,
                        contentDescription = "Area Size",
                        tint = FromchemTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = project.areaSize,
                        fontSize = 12.sp,
                        color = FromchemTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chemical Specification Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FromchemSurfaceVariant,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Chemical Spec",
                            tint = FromchemPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = project.chemicalUsed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = FromchemTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = project.description,
                    fontSize = 12.sp,
                    color = FromchemTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onInspectClick,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "View Photos",
                            tint = FromchemPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val totalCount = if (project.userUploadedOriginalUris.isNotEmpty()) {
                            project.userUploadedOriginalUris.size
                        } else {
                            project.photoCount
                        }
                        Text(
                            text = "View $totalCount Photos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FromchemPrimary
                        )
                    }

                    Button(
                        onClick = onGetQuoteClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Get Similar Quote",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen Lightbox Modal displaying original project photos in full resolution.
 * Displays user-uploaded images or stored project images directly.
 * Displays clear "Image unavailable" if no image exists. Never generates AI imagery.
 */
@Composable
fun ProjectLightboxDialog(
    project: GalleryProject,
    onDismiss: () -> Unit,
    onGetQuoteClick: () -> Unit
) {
    var lightboxTab by remember { mutableStateOf("after") }
    var activePhotoIndex by remember { mutableIntStateOf(0) }

    // Aggregate all original images in strict upload order
    val allOriginalUris = remember(project) {
        val list = mutableListOf<Uri>()
        project.userUploadedBeforeUri?.let { list.add(it) }
        project.userUploadedAfterUri?.let { list.add(it) }
        project.userUploadedComparisonUri?.let { list.add(it) }
        list.addAll(project.userUploadedOriginalUris)
        list.distinct()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Modal Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${project.location} • ${project.areaSize} • Real Site Photography",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Lightbox",
                            tint = Color.White
                        )
                    }
                }

                // Interactive Switcher (Before / After / Compare)
                val canCompare = project.userUploadedComparisonUri != null || project.comparisonRes != null
                val canBefore = project.userUploadedBeforeUri != null || project.beforeRes != null

                if (canCompare || canBefore) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (canBefore) {
                            Surface(
                                onClick = { lightboxTab = "before" },
                                shape = RoundedCornerShape(20.dp),
                                color = if (lightboxTab == "before") Color.White else Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Before",
                                    color = if (lightboxTab == "before") Color.Black else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Surface(
                            onClick = { lightboxTab = "after" },
                            shape = RoundedCornerShape(20.dp),
                            color = if (lightboxTab == "after") FromchemPrimary else Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "After",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        if (canCompare) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                onClick = { lightboxTab = "compare" },
                                shape = RoundedCornerShape(20.dp),
                                color = if (lightboxTab == "compare") Color(0xFF0D47A1) else Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Compare",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Direct Fullscreen Image Rendering Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E1E))
                ) {
                    if (allOriginalUris.isNotEmpty()) {
                        // User uploaded original photos displayed in exact order
                        val currentUri = allOriginalUris.getOrNull(activePhotoIndex) ?: allOriginalUris.first()
                        AuthoritativeProjectImage(
                            userUploadedUri = currentUri,
                            contentDescription = "${project.title} - Photo ${activePhotoIndex + 1}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Stored project photos with Before / After / Compare support
                        when (lightboxTab) {
                            "before" -> {
                                AuthoritativeProjectImage(
                                    storedResId = project.beforeRes,
                                    contentDescription = "Before Treatment",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            "compare" -> {
                                AuthoritativeProjectImage(
                                    storedResId = project.comparisonRes ?: project.afterRes ?: project.beforeRes,
                                    contentDescription = "Side-by-side comparison",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                AuthoritativeProjectImage(
                                    userUploadedUrl = project.originalImageUrl,
                                    storedResId = project.afterRes ?: project.beforeRes,
                                    contentDescription = "After Treatment",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Navigation Arrows for multi-photo uploaded gallery
                    if (allOriginalUris.size > 1) {
                        if (activePhotoIndex > 0) {
                            IconButton(
                                onClick = { activePhotoIndex-- },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Photo",
                                    tint = Color.White
                                )
                            }
                        }

                        if (activePhotoIndex < allOriginalUris.size - 1) {
                            IconButton(
                                onClick = { activePhotoIndex++ },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Photo",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Metadata Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TEST & COMPLIANCE RESULT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemAccentGreen
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = project.durability,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = project.testResult,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = project.description,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onGetQuoteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                ) {
                    Text(
                        text = "Request Technical Consultation & Quote",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Upload Real Project Photos Dialog.
 * Allows users to upload original site photos using Android's Photo Picker.
 * Preserves exact file references, order, and appearance without AI regeneration.
 */
@Composable
fun UploadProjectPhotosDialog(
    project: GalleryProject,
    onDismiss: () -> Unit,
    onPhotosUploaded: (GalleryProject) -> Unit
) {
    val context = LocalContext.current
    var uploadedBeforeUri by remember { mutableStateOf<Uri?>(project.userUploadedBeforeUri) }
    var uploadedAfterUri by remember { mutableStateOf<Uri?>(project.userUploadedAfterUri) }
    var uploadedList by remember { mutableStateOf<List<Uri>>(project.userUploadedOriginalUris) }

    // Android Photo Pickers for Before and After
    val beforePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedBeforeUri = uri
            Toast.makeText(context, "Original Before photo loaded", Toast.LENGTH_SHORT).show()
        }
    }

    val afterPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedAfterUri = uri
            Toast.makeText(context, "Original After photo loaded", Toast.LENGTH_SHORT).show()
        }
    }

    val multiplePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uploadedList = uris
            Toast.makeText(context, "${uris.size} original photos loaded", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Upload Original Project Photos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FromchemTextPrimary
                        )
                        Text(
                            text = "For ${project.title}",
                            fontSize = 12.sp,
                            color = FromchemTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Strict Real Photo Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Real Images",
                            tint = FromchemAccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Uploaded photos are stored as authoritative originals. No AI image generation or modification will be applied.",
                            fontSize = 11.sp,
                            color = Color(0xFF1B5E20),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Before Photo Picker Section
                Text(
                    text = "1. Before Photo (Raw Substrate / Leak)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FromchemSurfaceVariant)
                        .border(1.dp, FromchemBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            beforePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uploadedBeforeUri != null) {
                        AsyncImage(
                            model = uploadedBeforeUri,
                            contentDescription = "Uploaded Before Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick Before",
                                tint = FromchemPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select Before Photo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FromchemPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // After Photo Picker Section
                Text(
                    text = "2. After Photo (Completed Waterproofing)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FromchemSurfaceVariant)
                        .border(1.dp, FromchemBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            afterPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uploadedAfterUri != null) {
                        AsyncImage(
                            model = uploadedAfterUri,
                            contentDescription = "Uploaded After Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick After",
                                tint = FromchemPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select After Photo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FromchemPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multiple Photos Picker Section
                Text(
                    text = "3. Or Select Multiple Project Photos (In Exact Order)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        multiplePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = "Select multiple photos",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Choose up to 10 Original Photos")
                }

                if (uploadedList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uploadedList) { uri ->
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, FromchemBorder, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Uploaded photo preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Save Button
                Button(
                    onClick = {
                        // Persist all uploaded images locally into app private files directory permanently
                        val permanentBefore = uploadedBeforeUri?.let { GalleryStorageManager.persistImageLocally(context, it) } ?: project.userUploadedBeforeUri
                        val permanentAfter = uploadedAfterUri?.let { GalleryStorageManager.persistImageLocally(context, it) } ?: project.userUploadedAfterUri
                        val permanentList = uploadedList.mapNotNull { GalleryStorageManager.persistImageLocally(context, it) }
                        val combinedOriginal = (permanentList + project.userUploadedOriginalUris).distinct()

                        val updated = project.copy(
                            userUploadedBeforeUri = permanentBefore,
                            userUploadedAfterUri = permanentAfter,
                            userUploadedOriginalUris = if (combinedOriginal.isNotEmpty()) combinedOriginal else project.userUploadedOriginalUris,
                            photoCount = maxOf(
                                project.photoCount,
                                (if (permanentBefore != null) 1 else 0) +
                                        (if (permanentAfter != null) 1 else 0) +
                                        combinedOriginal.size
                            )
                        )
                        GalleryStorageManager.saveOrUpdateProject(context, updated)
                        onPhotosUploaded(updated)
                        Toast.makeText(context, "Real project photos permanently saved to gallery", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Photos",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Permanently to Gallery",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Add Gallery Image Dialog.
 * Allows adding a new project or photo entry from the "Add Gallery" section.
 * Photos are copied to local app internal storage and persisted permanently,
 * ensuring they remain available even after closing or restarting the app.
 */
@Composable
fun AddGalleryImageDialog(
    onDismiss: () -> Unit,
    onProjectCreated: (GalleryProject) -> Unit
) {
    val context = LocalContext.current
    var projectTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Roof Waterproofing") }
    var location by remember { mutableStateOf("Ahmedabad, Gujarat") }
    var areaSize by remember { mutableStateOf("25,000 sq ft") }
    var chemicalUsed by remember { mutableStateOf("Fromchem Polymeric Waterproofing Membrane") }
    var description by remember { mutableStateOf("Original site waterproofing application and barrier inspection.") }

    var beforeUri by remember { mutableStateOf<Uri?>(null) }
    var afterUri by remember { mutableStateOf<Uri?>(null) }
    var additionalUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSaving by remember { mutableStateOf(false) }

    val categories = listOf(
        "Roof Waterproofing",
        "Basement Tanking",
        "Bridge & Infrastructure",
        "Podiums & Plazas",
        "Wall Crack Repair",
        "General Waterproofing"
    )

    val templates = listOf(
        Triple("Skyline IT Tower Rooftop", "Roof Waterproofing", "Fromchem Pure Polyurea 2000"),
        Triple("Basement Retaining Wall Injection", "Basement Tanking", "Fromchem Crystalline Deep-Penetrant Slurry"),
        Triple("Highway Viaduct Bridge Deck", "Bridge & Infrastructure", "Fromchem Hydro-Shield Epoxy Sealant"),
        Triple("Regal Plaza Podium Deck", "Podiums & Plazas", "Fromchem Elastomeric PU Membrane"),
        Triple("Custom Project", "Roof Waterproofing", "Fromchem Chemical Waterproofing System")
    )

    // Photo pickers
    val beforePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            beforeUri = uri
            Toast.makeText(context, "Before photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    val afterPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            afterUri = uri
            Toast.makeText(context, "After photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    val multiPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            additionalUris = uris
            Toast.makeText(context, "${uris.size} photos selected", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Add to Project Gallery",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemTextPrimary
                            )
                            Text(
                                text = "Photos stay permanently after closing app",
                                fontSize = 11.sp,
                                color = FromchemTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Permanent Storage Assurance Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Permanent Storage",
                            tint = FromchemAccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "🔒 Permanent Storage: Photos added here are copied to app storage and will remain permanently even after closing or restarting the app.",
                            fontSize = 11.sp,
                            color = Color(0xFF1B5E20),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Templates
                Text(
                    text = "Quick Project Templates",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(templates) { (tTitle, tCat, tChem) ->
                        SuggestionChip(
                            onClick = {
                                projectTitle = if (tTitle == "Custom Project") "" else tTitle
                                selectedCategory = tCat
                                chemicalUsed = tChem
                            },
                            label = { Text(tTitle, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Project Title Field
                OutlinedTextField(
                    value = projectTitle,
                    onValueChange = { projectTitle = it },
                    label = { Text("Project Title *") },
                    placeholder = { Text("e.g. Skyline Rooftop Waterproofing") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category selector
                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FromchemPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Location Field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Site Location") },
                    placeholder = { Text("e.g. Ahmedabad, Gujarat") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Photo 1: Before Photo
                Text(
                    text = "1. Before Photo (Raw Substrate / Leak)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            beforePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (beforeUri == null) "Select Before Photo" else "Change Before Photo", fontSize = 12.sp)
                    }
                    if (beforeUri != null) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, FromchemPrimary, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = beforeUri,
                                contentDescription = "Before preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(onClick = { beforeUri = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Photo 2: After Photo
                Text(
                    text = "2. After Photo (Completed Waterproofing Barrier)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            afterPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (afterUri == null) "Select After Photo" else "Change After Photo", fontSize = 12.sp)
                    }
                    if (afterUri != null) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, FromchemPrimary, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = afterUri,
                                contentDescription = "After preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(onClick = { afterUri = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Photo 3: Additional Photos
                Text(
                    text = "3. Additional Site Photos (Optional, up to 10 photos)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        multiPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (additionalUris.isEmpty()) "Choose Additional Photos" else "${additionalUris.size} Photos Selected", fontSize = 12.sp)
                }

                if (additionalUris.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(additionalUris) { uri ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, FromchemBorder, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Photo preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val hasAtLeastOnePhoto = beforeUri != null || afterUri != null || additionalUris.isNotEmpty()
                val canSave = projectTitle.isNotBlank() && hasAtLeastOnePhoto && !isSaving

                Button(
                    onClick = {
                        if (!canSave) {
                            if (projectTitle.isBlank()) {
                                Toast.makeText(context, "Please enter a project title", Toast.LENGTH_SHORT).show()
                            } else if (!hasAtLeastOnePhoto) {
                                Toast.makeText(context, "Please select at least one photo", Toast.LENGTH_SHORT).show()
                            }
                            return@Button
                        }
                        isSaving = true

                        // Persist all images locally into app private files directory
                        val permanentBefore = beforeUri?.let { GalleryStorageManager.persistImageLocally(context, it) }
                        val permanentAfter = afterUri?.let { GalleryStorageManager.persistImageLocally(context, it) }
                        val permanentAdditional = additionalUris.mapNotNull { GalleryStorageManager.persistImageLocally(context, it) }

                        val allPhotos = mutableListOf<Uri>()
                        permanentBefore?.let { allPhotos.add(it) }
                        permanentAfter?.let { allPhotos.add(it) }
                        allPhotos.addAll(permanentAdditional)

                        val newProject = GalleryProject(
                            id = "proj_${System.currentTimeMillis()}",
                            title = projectTitle.trim(),
                            category = selectedCategory,
                            location = location.trim().ifEmpty { "Site Location" },
                            areaSize = areaSize,
                            chemicalUsed = chemicalUsed,
                            durability = "20 Years Warranty",
                            description = description,
                            testResult = "Hydrostatic Water Resistance Tested & Certified",
                            photoCount = allPhotos.size,
                            primaryColor = Color(0xFF0288D1),
                            secondaryColor = Color(0xFF00ACC1),
                            beforeRes = null,
                            afterRes = null,
                            comparisonRes = null,
                            userUploadedBeforeUri = permanentBefore,
                            userUploadedAfterUri = permanentAfter ?: permanentAdditional.firstOrNull(),
                            userUploadedComparisonUri = null,
                            userUploadedOriginalUris = allPhotos.distinct()
                        )

                        GalleryStorageManager.saveOrUpdateProject(context, newProject)
                        onProjectCreated(newProject)
                        Toast.makeText(context, "Photo permanently added to gallery!", Toast.LENGTH_SHORT).show()
                    },
                    enabled = canSave,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaving) "Saving Permanently..." else "Save to Permanent Gallery",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
