package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.launch

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
    val secondaryColor: Color
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
        photoCount = 4,
        primaryColor = Color(0xFF0288D1),
        secondaryColor = Color(0xFF00ACC1)
    ),
    GalleryProject(
        id = "proj_02",
        title = "Metro Rail Tunnel & Underground Station",
        category = "Basement Tanking",
        location = "Surat, Gujarat",
        areaSize = "1,20,000 sq ft",
        chemicalUsed = "Fromchem Crystalline Deep-Penetrant Slurry",
        durability = "Lifetime Concrete Self-Healing",
        description = "Deep reactive chemical slurry reacting with moisture to grow insoluble needle-like crystals, permanently blocking capillary pores against high water pressure.",
        testResult = "12 Bar Positive & Negative Hydrostatic Pressure Tested",
        photoCount = 5,
        primaryColor = Color(0xFF388E3C),
        secondaryColor = Color(0xFF66BB6A)
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
        photoCount = 3,
        primaryColor = Color(0xFFD32F2F),
        secondaryColor = Color(0xFFE53935)
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
        photoCount = 4,
        primaryColor = Color(0xFF7B1FA2),
        secondaryColor = Color(0xFFAB47BC)
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
        photoCount = 3,
        primaryColor = Color(0xFFF57C00),
        secondaryColor = Color(0xFFFFB74D)
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
        photoCount = 4,
        primaryColor = Color(0xFF00796B),
        secondaryColor = Color(0xFF26A69A)
    )
)

@Composable
fun ProjectGallerySection(
    onGetQuoteForProject: (String) -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All Projects", "Roof Waterproofing", "Basement Tanking", "Bridge & Infrastructure", "Podiums & Plazas")
    var selectedCategory by remember { mutableStateOf("All Projects") }

    val filteredProjects = remember(selectedCategory) {
        if (selectedCategory == "All Projects") {
            sampleGalleryProjects
        } else {
            sampleGalleryProjects.filter { it.category == selectedCategory }
        }
    }

    val pagerState = rememberPagerState(pageCount = { filteredProjects.size })
    val coroutineScope = rememberCoroutineScope()

    var activeLightboxProject by remember { mutableStateOf<GalleryProject?>(null) }

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
                        text = "REAL WORK & VISUAL QUALITY",
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
                text = "Swipe through our completed chemical waterproofing applications across industrial, commercial, and infrastructure sites.",
                fontSize = 13.sp,
                color = FromchemTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter Chips
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

        if (filteredProjects.isNotEmpty()) {
            // Gallery Swiper Pager
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 480.dp else 450.dp)
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

            // Page Indicator Dots & Swipe Hint
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
}

@Composable
fun GalleryProjectCard(
    project: GalleryProject,
    onInspectClick: () -> Unit,
    onGetQuoteClick: () -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    var showAfterView by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, FromchemBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Visual Banner Graphic with Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 210.dp else 180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                project.primaryColor.copy(alpha = 0.85f),
                                project.secondaryColor.copy(alpha = 0.95f)
                            )
                        )
                    )
            ) {
                // Chemical Application Canvas Representation
                ProjectChemicalCanvasGraphic(
                    project = project,
                    isAfter = showAfterView,
                    modifier = Modifier.fillMaxSize()
                )

                // Category & Badge Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = project.category,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

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
                }

                // Interactive Before / After Toggle Switch Button
                Surface(
                    onClick = { showAfterView = !showAfterView },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.75f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Compare View",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showAfterView) "Viewing: Chemical Sealed (After)" else "Viewing: Untreated Surface (Before)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Fullscreen Zoom Icon Button
                IconButton(
                    onClick = onInspectClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Expand,
                        contentDescription = "Fullscreen Photo Gallery",
                        tint = FromchemPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Project Details Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = project.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            imageVector = Icons.Default.Engineering,
                            contentDescription = "Area",
                            tint = FromchemAccentGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = project.areaSize,
                            fontSize = 12.sp,
                            color = FromchemTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = project.description,
                        fontSize = 12.sp,
                        color = FromchemTextPrimary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chemical Spec Tag
                    Surface(
                        color = FromchemPrimaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Chemical",
                                tint = FromchemPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = project.chemicalUsed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onInspectClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FromchemBorder, FromchemBorder)))
                    ) {
                        Text(text = "View Gallery (${project.photoCount})", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onGetQuoteClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                    ) {
                        Text(text = "Similar Quote", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectChemicalCanvasGraphic(
    project: GalleryProject,
    isAfter: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        if (!isAfter) {
            // UNTREATED / BEFORE STATE: Concrete slab texture with water crack lines
            drawRect(color = Color(0xFF616161))

            // Concrete aggregate specks
            for (i in 0..40) {
                val cx = (i * 37) % width
                val cy = (i * 29) % height
                drawCircle(
                    color = Color(0xFF424242),
                    radius = 3f,
                    center = Offset(cx, cy)
                )
            }

            // Water leakage crack path
            val crackPath = Path().apply {
                moveTo(width * 0.2f, 0f)
                lineTo(width * 0.25f, height * 0.3f)
                lineTo(width * 0.22f, height * 0.6f)
                lineTo(width * 0.35f, height)
            }

            drawPath(
                path = crackPath,
                color = Color(0xFF212121),
                style = Stroke(width = 4f)
            )

            // Leakage water drop pooling
            drawCircle(
                color = Color(0xFF0288D1).copy(alpha = 0.6f),
                radius = 20f,
                center = Offset(width * 0.25f, height * 0.45f)
            )
        } else {
            // AFTER STATE: Glossy seamless chemical coating with crystalline reaction nodes
            val gradient = Brush.linearGradient(
                colors = listOf(project.primaryColor, project.secondaryColor),
                start = Offset.Zero,
                end = Offset(width, height)
            )
            drawRect(brush = gradient)

            // Monolithic chemical wave sheen lines
            val wavePath = Path().apply {
                moveTo(0f, height * 0.4f)
                cubicTo(
                    width * 0.3f, height * 0.2f,
                    width * 0.7f, height * 0.6f,
                    width, height * 0.35f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = wavePath,
                color = Color.White.copy(alpha = 0.15f)
            )

            // Chemical molecular sealing nodes
            for (i in 0..15) {
                val nx = (i * 67 + 30) % width
                val ny = (i * 43 + 20) % height
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = 8f,
                    center = Offset(nx, ny)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = 3f,
                    center = Offset(nx, ny)
                )
            }

            // Glossy highlight reflection bar
            drawRect(
                color = Color.White.copy(alpha = 0.18f),
                topLeft = Offset(width * 0.6f, 0f),
                size = Size(width * 0.15f, height)
            )
        }
    }
}

@Composable
fun ProjectLightboxDialog(
    project: GalleryProject,
    onDismiss: () -> Unit,
    onGetQuoteClick: () -> Unit
) {
    var activePhotoIndex by remember { mutableIntStateOf(0) }

    val photoTitles = listOf(
        "1. Surface Preparation & Sanding",
        "2. Spray Application of Chemical Sealant",
        "3. Monolithic Cured Membrane Finish",
        "4. Hydrostatic Ponding Test Inspection",
        "5. Final Client Handover & ISO Certificate"
    ).take(project.photoCount)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Lightbox Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = project.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${project.location} • Photo ${activePhotoIndex + 1} of ${project.photoCount}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
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

                Spacer(modifier = Modifier.height(16.dp))

                // High-Res Canvas Image Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E1E))
                ) {
                    ProjectChemicalCanvasGraphic(
                        project = project,
                        isAfter = activePhotoIndex % 2 == 0,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Photo Step Title Ribbon
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = photoTitles.getOrElse(activePhotoIndex) { "Inspection Photo" },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    // Left Photo Prev Arrow
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

                    // Right Photo Next Arrow
                    if (activePhotoIndex < project.photoCount - 1) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Specs & Test Info
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF262626)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Passed Test",
                                tint = FromchemAccentGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = project.testResult,
                                color = FromchemAccentGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = project.description,
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chemical: ${project.chemicalUsed}",
                                color = FromchemPrimaryContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Button(
                                onClick = onGetQuoteClick,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                            ) {
                                Text("Get Quote for This Solution", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
