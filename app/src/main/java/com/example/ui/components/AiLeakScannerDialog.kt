package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.GeminiChatService
import com.example.ai.GeminiLeakAnalysisResult
import com.example.data.FirebasePhotoManager
import com.example.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class StructuralPreset(
    val id: String,
    val name: String,
    val category: String,
    val detectedIssue: String,
    val recommendedApplication: String,
    val suggestedNextStep: String,
    val severityLevel: String,
    val severityColor: Color,
    val chemicalSpec: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val sampleStructuralPresets = listOf(
    StructuralPreset(
        id = "preset_wall_crack",
        name = "Wall Crack",
        category = "Masonry Crack",
        detectedIssue = "Vertical Shear Crack & Capillary Water Seepage",
        recommendedApplication = "Fromchem Hydro-Flex Polyurethane Crack Injection & Sealant",
        suggestedNextStep = "Request Professional On-Site Structural Crack Inspection",
        severityLevel = "Moderate Risk",
        severityColor = Color(0xFFF57C00),
        chemicalSpec = "High Elongation PU Foam & Elastomeric Putty",
        description = "Thermal contraction crack allowing rainwater ingress through outer plaster. Requires pressure injection and elastomeric waterproofing seal.",
        icon = Icons.Default.BrokenImage
    ),
    StructuralPreset(
        id = "preset_ceiling_leak",
        name = "Ceiling Leakage",
        category = "Inter-Floor Leakage",
        detectedIssue = "Overhead Slab Porosity & Active Damp Spot dripping",
        recommendedApplication = "Fromchem Crystalline Deep-Penetrant Slurry + Acrylic Shield",
        suggestedNextStep = "Conduct Upper Slab Ponding Test & Crystalline Barrier Application",
        severityLevel = "High Urgency",
        severityColor = Color(0xFFD32F2F),
        chemicalSpec = "Self-Healing Catalytic Micro-Crystals",
        description = "Water penetrating from bathroom/terrace slab above. Micro-crystalline slurry reacts with free lime to permanently fill capillary pores.",
        icon = Icons.Default.WaterDrop
    ),
    StructuralPreset(
        id = "preset_damp_wall",
        name = "Damp Wall",
        category = "Efflorescence",
        detectedIssue = "Rising Dampness & Salt Efflorescence Peeling Paint",
        recommendedApplication = "Fromchem Aquablock Damp-Proof Liquid Primer & Anti-Salt Coat",
        suggestedNextStep = "Apply Chemical Damp Barrier prior to Re-Plastering",
        severityLevel = "Moderate Risk",
        severityColor = Color(0xFFF57C00),
        chemicalSpec = "Silane-Siloxane Hydrophobic Nanotechnology",
        description = "Ground capillary moisture rising through brickwork, bringing mineral salts that push off internal wall paint and plaster.",
        icon = Icons.Default.Opacity
    ),
    StructuralPreset(
        id = "preset_terrace",
        name = "Terrace Slab",
        category = "Roof Deck",
        detectedIssue = "Terrace Water Ponding & Bitumen Joint Deterioration",
        recommendedApplication = "Fromchem Pure Polyurea 2000 Liquid Spray Membrane",
        suggestedNextStep = "Schedule Monolithic Seamless Spraying for Entire Roof",
        severityLevel = "High Urgency",
        severityColor = Color(0xFFD32F2F),
        chemicalSpec = "10-Second Rapid Curing Monolithic Elastomeric",
        description = "Stagnant rainwater pooling over damaged brick bat coba joints. Requires high-durability jointless elastomeric polyurea coating.",
        icon = Icons.Default.Roofing
    ),
    StructuralPreset(
        id = "preset_basement",
        name = "Basement Wall",
        category = "Underground Foundation",
        detectedIssue = "Negative Hydrostatic Water Pressure & Construction Joint Leak",
        recommendedApplication = "Fromchem Negative Side Crystalline Tanking System",
        suggestedNextStep = "Request Heavy-Duty Underground Water Leak Grouting",
        severityLevel = "Severe Risk",
        severityColor = Color(0xFFB71C1C),
        chemicalSpec = "12-Bar Negative Water Pressure Resistance",
        description = "High groundwater pressure forcing water through retaining wall cold joints. Requires deep penetrating catalytic crystalline treatment.",
        icon = Icons.Default.Foundation
    )
)

@Composable
fun AiLeakScannerDialog(
    onDismiss: () -> Unit,
    onRequestInspection: (issueTitle: String, solution: String) -> Unit,
    currentUser: UserProfile? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPreset by remember { mutableStateOf<StructuralPreset?>(sampleStructuralPresets[0]) }

    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisStatusText by remember { mutableStateOf("Initializing AI Analysis...") }
    var analysisProgress by remember { mutableFloatStateOf(0f) }
    var analysisComplete by remember { mutableStateOf(false) }

    // Dynamic result from Gemini analysis
    var activeGeminiResult by remember { mutableStateOf<GeminiLeakAnalysisResult?>(null) }
    var firebaseSyncStatus by remember { mutableStateOf<String?>(null) }
    var firebaseDocId by remember { mutableStateOf<String?>(null) }

    var currentScanJob by remember { mutableStateOf<Job?>(null) }

    // Helper to run the integrated Firebase + Gemini analysis
    fun executeIntegratedScan(
        imageBytes: ByteArray?,
        bitmap: Bitmap?,
        preset: StructuralPreset?
    ) {
        currentScanJob?.cancel()
        currentScanJob = coroutineScope.launch {
            isAnalyzing = true
            analysisComplete = false
            analysisProgress = 0.1f
            analysisStatusText = "Preparing image data..."
            firebaseSyncStatus = null
            firebaseDocId = null

            var storageUrl: String? = null
            var storagePath: String? = null
            var storageError: String? = null

            // 1. Firebase Cloud Storage Upload (if image exists)
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                analysisStatusText = "Uploading to Firebase Cloud Storage..."
                analysisProgress = 0.25f
                val fileName = "leak_inspection_${System.currentTimeMillis()}.jpg"
                val explicitUserId = currentUser?.email ?: FirebasePhotoManager.getCurrentUserId()

                val storageUploadRes = FirebasePhotoManager.uploadImageToStorage(
                    context = context,
                    imageBytes = imageBytes,
                    rawFileName = fileName,
                    explicitUserId = explicitUserId
                )

                if (storageUploadRes.isSuccess) {
                    val pair = storageUploadRes.getOrNull()
                    storageUrl = pair?.first
                    storagePath = pair?.second
                    Log.d("AiLeakScanner", "Firebase Storage upload succeeded: $storageUrl")
                } else {
                    storageError = storageUploadRes.exceptionOrNull()?.message ?: "Upload failed"
                    Log.w("AiLeakScanner", "Firebase Storage upload failed: $storageError")
                }
            }

            // 2. Google Gemini AI Analysis Flow
            analysisStatusText = "Running Gemini AI chemical leak analysis..."
            analysisProgress = 0.60f
            val contextDesc = preset?.let { "${it.name} - ${it.category}: ${it.description}" }
                ?: "Building leak / concrete moisture defect inspection"

            val geminiAnalysisRes = GeminiChatService.analyzeLeakPhoto(
                imageBytes = imageBytes,
                contextDescription = contextDesc
            )

            val geminiResult = geminiAnalysisRes.getOrDefault(
                preset?.let {
                    GeminiLeakAnalysisResult(
                        detectedIssue = it.detectedIssue,
                        recommendedApplication = it.recommendedApplication,
                        suggestedNextStep = it.suggestedNextStep,
                        severityLevel = it.severityLevel,
                        chemicalSpec = it.chemicalSpec,
                        summary = it.description
                    )
                } ?: GeminiChatService.getDefaultLeakAnalysis(contextDesc)
            )

            activeGeminiResult = geminiResult

            // 3. Save Record in Cloud Firestore (photos collection)
            analysisStatusText = "Saving diagnostic to Cloud Firestore..."
            analysisProgress = 0.85f

            if (storageUrl != null && storagePath != null) {
                val firestoreRes = FirebasePhotoManager.savePhotoRecordToFirestore(
                    context = context,
                    imageUrl = storageUrl,
                    fileName = storagePath.substringAfterLast("/"),
                    storagePath = storagePath,
                    geminiResult = geminiResult,
                    explicitUserId = currentUser?.email ?: FirebasePhotoManager.getCurrentUserId(),
                    status = if (geminiAnalysisRes.isSuccess) "success" else "gemini_failed"
                )

                if (firestoreRes.isSuccess) {
                    firebaseDocId = firestoreRes.getOrNull()
                    firebaseSyncStatus = "Synced to Firebase Storage & Firestore"
                } else {
                    firebaseSyncStatus = "Storage uploaded (Firestore offline)"
                }
            } else if (storageError != null) {
                firebaseSyncStatus = "Analysis complete (Firebase offline)"
            }

            delay(300)
            analysisProgress = 1.0f
            isAnalyzing = false
            analysisComplete = true
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedImageUri = null
            selectedPreset = null
            val bytes = FirebasePhotoManager.bitmapToByteArray(bitmap)
            executeIntegratedScan(bytes, bitmap, null)
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            selectedPreset = null
            val loadedBmp = try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                null
            }
            capturedBitmap = loadedBmp
            val bytes = FirebasePhotoManager.uriToByteArray(context, uri)
                ?: loadedBmp?.let { FirebasePhotoManager.bitmapToByteArray(it) }
            executeIntegratedScan(bytes, loadedBmp, null)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
                .wrapContentHeight()
                .padding(vertical = 12.dp)
                .testTag("ai_leak_scanner_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = FromchemPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera AI",
                                    tint = FromchemPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI Water Leak Inspector",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FromchemPrimary
                                ) {
                                    Text(
                                        text = "GEMINI AI",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Instant Chemical Diagnostics for Cracks & Dampness",
                                fontSize = 11.sp,
                                color = FromchemTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FromchemSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = FromchemTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Camera / Upload Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { cameraLauncher.launch() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("take_photo_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("upload_photo_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(FromchemPrimary)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "Upload Photo",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Image", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sample Presets Selection Bar
                Text(
                    text = "OR SELECT SAMPLE STRUCTURE FOR INSTANT DEMO:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleStructuralPresets) { preset ->
                        val isSelected = selectedPreset?.id == preset.id && capturedBitmap == null && selectedImageUri == null
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPreset = preset
                                capturedBitmap = null
                                selectedImageUri = null
                                executeIntegratedScan(null, null, preset)
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(preset.name, fontSize = 11.sp)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FromchemPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = FromchemSurfaceVariant,
                                labelColor = FromchemTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Display Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FromchemBorder, RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Leak Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (selectedPreset != null) {
                        // Canvas Graphic representation for selected structure preset
                        PresetStructureGraphic(
                            preset = selectedPreset!!,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Take a picture of wall crack, ceiling dampness, terrace, or basement",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Scanning Overlay Effect when analyzing
                    if (isAnalyzing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.70f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { analysisProgress },
                                    color = FromchemPrimary,
                                    trackColor = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = analysisStatusText,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Detecting capillary pore depth, moisture, & slab tension",
                                    color = Color.LightGray,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnostic Result View
                val currentPreset = selectedPreset ?: sampleStructuralPresets[0]
                val currentDetectedIssue = activeGeminiResult?.detectedIssue ?: currentPreset.detectedIssue
                val currentRecommendedApplication = activeGeminiResult?.recommendedApplication ?: currentPreset.recommendedApplication
                val currentSuggestedNextStep = activeGeminiResult?.suggestedNextStep ?: currentPreset.suggestedNextStep
                val currentSeverityLevel = activeGeminiResult?.severityLevel ?: currentPreset.severityLevel
                val currentSummary = activeGeminiResult?.summary ?: currentPreset.description

                val severityColor = when {
                    currentSeverityLevel.contains("Severe", ignoreCase = true) -> Color(0xFFB71C1C)
                    currentSeverityLevel.contains("High", ignoreCase = true) -> Color(0xFFD32F2F)
                    else -> Color(0xFFF57C00)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_diagnostic_result")
                ) {
                    // Result Header & Severity Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = FromchemPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI DIAGNOSTIC REPORT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = severityColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = currentSeverityLevel,
                                color = severityColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Firebase Sync Status indicator
                    if (firebaseSyncStatus != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (firebaseDocId != null) Color(0xFFE8F5E9) else FromchemSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (firebaseDocId != null) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = if (firebaseDocId != null) FromchemAccentGreen else FromchemTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = firebaseSyncStatus!!,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (firebaseDocId != null) FromchemAccentGreen else FromchemTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Detected Issue
                    DiagnosticFieldCard(
                        icon = Icons.Default.Search,
                        label = "Detected Issue",
                        value = currentDetectedIssue,
                        valueColor = FromchemTextPrimary,
                        containerColor = FromchemSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Recommended Application
                    DiagnosticFieldCard(
                        icon = Icons.Default.Science,
                        label = "Recommended Chemical Application",
                        value = currentRecommendedApplication,
                        valueColor = FromchemPrimary,
                        containerColor = FromchemPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Suggested Next Step
                    DiagnosticFieldCard(
                        icon = Icons.Default.FactCheck,
                        label = "Suggested Next Step",
                        value = currentSuggestedNextStep,
                        valueColor = FromchemAccentGreen,
                        containerColor = Color(0xFFE8F5E9)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Technical Detail Spec Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "CHEMICAL DIAGNOSTIC SUMMARY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemTextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentSummary,
                                fontSize = 12.sp,
                                color = FromchemTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Technical Data Sheet for $currentRecommendedApplication downloaded!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("download_spec_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(FromchemBorder)
                            )
                        ) {
                            Text("Download Specs", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onRequestInspection(currentDetectedIssue, currentRecommendedApplication)
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("request_inspection_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary)
                        ) {
                            Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request Inspection", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticFieldCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    containerColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = valueColor,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PresetStructureGraphic(
    preset: StructuralPreset,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        when (preset.id) {
            "preset_wall_crack" -> {
                // Brickwork masonry background
                drawRect(color = Color(0xFF475569))
                for (r in 0..5) {
                    val y = r * (height / 5f)
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 3f
                    )
                }

                // Vertical Jagged Crack Path
                val crack = Path().apply {
                    moveTo(width * 0.45f, 0f)
                    lineTo(width * 0.42f, height * 0.25f)
                    lineTo(width * 0.52f, height * 0.55f)
                    lineTo(width * 0.48f, height * 0.85f)
                    lineTo(width * 0.5f, height)
                }
                drawPath(crack, color = Color(0xFF0F172A), style = Stroke(width = 6f))

                // Water moisture halo around crack
                drawPath(crack, color = Color(0xFF0288D1).copy(alpha = 0.4f), style = Stroke(width = 16f))
            }
            "preset_ceiling_leak" -> {
                // Concrete ceiling slab with peeling paint
                drawRect(color = Color(0xFF334155))

                // Damp water patch
                drawCircle(
                    color = Color(0xFF0288D1).copy(alpha = 0.5f),
                    radius = height * 0.35f,
                    center = Offset(width * 0.5f, height * 0.4f)
                )

                // Dripping water droplets
                for (i in 1..4) {
                    val dx = width * (0.2f * i + 0.1f)
                    val dy = height * 0.65f + (i * 10f)
                    drawCircle(color = Color(0xFF38BDF8), radius = 6f, center = Offset(dx, dy))
                }
            }
            "preset_damp_wall" -> {
                // Plaster wall with salt efflorescence spots
                drawRect(color = Color(0xFF64748B))

                // White mineral salt crystal patches
                for (i in 0..20) {
                    val cx = (i * 47) % width
                    val cy = (i * 31) % height
                    drawCircle(color = Color.White.copy(alpha = 0.6f), radius = (i % 5 + 4).toFloat(), center = Offset(cx, cy))
                }
            }
            "preset_terrace" -> {
                // Terrace roof slab with standing water pond
                drawRect(color = Color(0xFF1E293B))

                // Stagnant water pool
                val pool = Path().apply {
                    moveTo(width * 0.1f, height * 0.4f)
                    cubicTo(width * 0.4f, height * 0.2f, width * 0.8f, height * 0.6f, width * 0.9f, height * 0.5f)
                    lineTo(width * 0.9f, height * 0.85f)
                    lineTo(width * 0.1f, height * 0.85f)
                    close()
                }
                drawPath(pool, color = Color(0xFF0288D1).copy(alpha = 0.7f))
            }
            else -> {
                // Basement underground retaining wall
                drawRect(color = Color(0xFF0F172A))
                drawRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(width * 0.1f, height * 0.1f),
                    size = Size(width * 0.8f, height * 0.8f)
                )

                // Hydrostatic pressure arrows
                for (i in 1..3) {
                    val y = height * (0.25f * i)
                    drawLine(color = Color(0xFF38BDF8), start = Offset(0f, y), end = Offset(width * 0.18f, y), strokeWidth = 5f)
                }
            }
        }
    }
}

