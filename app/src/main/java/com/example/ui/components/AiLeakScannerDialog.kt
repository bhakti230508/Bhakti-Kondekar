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
import androidx.compose.animation.core.*
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
import com.example.ai.DetectedDefect
import com.example.ai.DefectBoundingBox
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
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val defects: List<DetectedDefect> = emptyList()
)

val sampleStructuralPresets = listOf(
    StructuralPreset(
        id = "preset_wall_crack",
        name = "Wall Crack",
        category = "Masonry Crack",
        detectedIssue = "Vertical Settlement Wall Crack",
        recommendedApplication = "Crack Paste (Fromchem Polymeric Waterproofing Crack Paste)",
        suggestedNextStep = "V-Groove Opening & Deep Application of Crack Paste",
        severityLevel = "Moderate Risk",
        severityColor = Color(0xFFF57C00),
        chemicalSpec = "Polymeric Waterproofing Crack Paste with High Elasticity",
        description = "Masonry and plaster cracks permitting moisture migration. High-grade Crack Paste fills structural voids, prevents micro-capillary seepage, and flexes with thermal expansion.",
        icon = Icons.Default.BrokenImage,
        defects = listOf(
            DetectedDefect(
                id = "preset_defect_1",
                problemTitle = "Vertical Settlement Wall Crack",
                shortLabel = "Wall Crack",
                location = "Central vertical wall section",
                severity = "MODERATE",
                confidenceScore = 95,
                visualEvidence = "Distinct linear fracture splitting wall plaster and substrate vertically.",
                likelyCause = "Substrate settlement or thermal expansion and contraction (recommend monitoring for growth).",
                recommendedAction = "Chisel V-groove profile (approx 5mm x 5mm), blow out debris, and inject high-elasticity polymeric filler.",
                fromchemSolution = "Crack Paste (Fromchem Polymeric Waterproofing Crack Paste)",
                fromchemProductSpec = "Polymeric Waterproofing Crack Paste with High Elasticity",
                boundingBox = DefectBoundingBox(0.08f, 0.38f, 0.92f, 0.62f)
            ),
            DetectedDefect(
                id = "preset_defect_2",
                problemTitle = "Branching Hairline Plaster Fissures",
                shortLabel = "Hairline Crack",
                location = "Right-hand adjacent masonry surface",
                severity = "LOW",
                confidenceScore = 88,
                visualEvidence = "Fine branching micro-fissures radiating from primary settlement crack.",
                likelyCause = "Plaster drying shrinkage and secondary stress relief.",
                recommendedAction = "Clean surface and skim with flexible crack filler prior to repainting.",
                fromchemSolution = "Crack Paste (Fromchem Polymeric Waterproofing Crack Paste)",
                fromchemProductSpec = "Polymeric Crack Paste",
                boundingBox = DefectBoundingBox(0.35f, 0.58f, 0.72f, 0.88f)
            )
        )
    ),
    StructuralPreset(
        id = "preset_ceiling_leak",
        name = "Ceiling Leakage",
        category = "Inter-Floor Leakage",
        detectedIssue = "Overhead Slab Porosity & Water Droplet Dripping",
        recommendedApplication = "Elastomeric Rubber Coating / 2-K Coating / White Membrane",
        suggestedNextStep = "Apply Multi-Layer 2-K Polymer Slurry or White Elastomeric Membrane",
        severityLevel = "High Urgency",
        severityColor = Color(0xFFD32F2F),
        chemicalSpec = "Elastomeric Rubberized Polymer / Two-Component 2-K / Reflective White Membrane",
        description = "Inter-floor slab water penetration. Highly flexible Elastomeric Rubber Coating, high-bond 2-K Acrylic-Cementitious Coating, or UV-resistant White Membrane stops overhead moisture ingress completely.",
        icon = Icons.Default.WaterDrop,
        defects = listOf(
            DetectedDefect(
                id = "preset_ceil_1",
                problemTitle = "Overhead Slab Porosity & Water Staining",
                shortLabel = "Ceiling Leak",
                location = "Central overhead concrete slab",
                severity = "HIGH",
                confidenceScore = 94,
                visualEvidence = "Circular discolored water stain with peeling paint halo on the ceiling.",
                likelyCause = "Water seepage from upper floor wet area, terrace ponding, or plumbing pipe run (exact ingress point unconfirmed without overhead access).",
                recommendedAction = "Investigate source on upper floor, seal micro-capillaries, and apply flexible waterproof slurry.",
                fromchemSolution = "Elastomeric Rubber Coating or 2-K Coating or White Membrane",
                fromchemProductSpec = "Two-Component 2-K Acrylic-Cementitious Coating / Reflective White Membrane",
                boundingBox = DefectBoundingBox(0.12f, 0.18f, 0.68f, 0.82f)
            ),
            DetectedDefect(
                id = "preset_ceil_2",
                problemTitle = "Active Water Droplet Dripping",
                shortLabel = "Active Drip",
                location = "Lower contour of ceiling stain",
                severity = "HIGH",
                confidenceScore = 91,
                visualEvidence = "Active suspended water droplet formation at low point of the ceiling.",
                likelyCause = "Saturated concrete slab allowing gravity water transit through micro-pores.",
                recommendedAction = "Relieve localized moisture pressure and coat overhead with high-elongation elastomeric membrane.",
                fromchemSolution = "Elastomeric Rubber Coating or 2-K Coating or White Membrane",
                fromchemProductSpec = "Elastomeric Rubberized Polymer Membrane",
                boundingBox = DefectBoundingBox(0.58f, 0.32f, 0.88f, 0.68f)
            )
        )
    ),
    StructuralPreset(
        id = "preset_damp_wall",
        name = "Damp Wall",
        category = "Efflorescence",
        detectedIssue = "Capillary Rising Dampness & Paint Blistering",
        recommendedApplication = "SBR Coating / Epoxy / PU (Polyurethane Coating)",
        suggestedNextStep = "Scrape Peeling Plaster & Apply Deep-Penetrating SBR / Epoxy / PU Barrier",
        severityLevel = "Moderate Risk",
        severityColor = Color(0xFFF57C00),
        chemicalSpec = "Styrene-Butadiene Rubber (SBR) / Chemical-Resistant Epoxy / Aliphatic PU",
        description = "Ground capillary moisture rising through masonry. High-performance SBR bonding coating, non-porous Epoxy barrier, or flexible Polyurethane (PU) protective film permanently blocks dampness.",
        icon = Icons.Default.Opacity,
        defects = listOf(
            DetectedDefect(
                id = "preset_damp_1",
                problemTitle = "Capillary Rising Dampness & Paint Blistering",
                shortLabel = "Rising Damp",
                location = "Lower wall section along baseboard",
                severity = "MODERATE",
                confidenceScore = 96,
                visualEvidence = "Persistent dark moisture tide-line and bubbling surface paint.",
                likelyCause = "Capillary action drawing ground moisture up through porous brickwork and plaster.",
                recommendedAction = "Scrape blistered paint and apply deep-penetrating SBR or epoxy damp-proof barrier.",
                fromchemSolution = "SBR Coating or Epoxy or PU (Fromchem Damp-Proofing System)",
                fromchemProductSpec = "Styrene-Butadiene Rubber (SBR) / Chemical-Resistant Epoxy / Aliphatic PU",
                boundingBox = DefectBoundingBox(0.48f, 0.10f, 0.94f, 0.90f)
            ),
            DetectedDefect(
                id = "preset_damp_2",
                problemTitle = "Efflorescence & White Mineral Salt Deposits",
                shortLabel = "Efflorescence",
                location = "Mid-to-lower wall surface perimeter",
                severity = "LOW",
                confidenceScore = 90,
                visualEvidence = "Crystalline white salt crusting along edge of the damp patch.",
                likelyCause = "Dissolved sub-surface mineral salts migrating with moisture and crystalizing during evaporation.",
                recommendedAction = "Dry-brush salt deposits and apply chemical neutralizer wash.",
                fromchemSolution = "SBR Coating or Epoxy or PU (Fromchem Damp-Proofing System)",
                fromchemProductSpec = "SBR Slurry with Anti-Efflorescence Primer",
                boundingBox = DefectBoundingBox(0.25f, 0.15f, 0.55f, 0.85f)
            )
        )
    ),
    StructuralPreset(
        id = "preset_basement",
        name = "Basement Wall",
        category = "Underground Foundation",
        detectedIssue = "Negative Hydrostatic Water Pressure & Foundation Seepage",
        recommendedApplication = "Black Membrane (Heavy-Duty Bituminous Tanking Membrane)",
        suggestedNextStep = "Install Continuous Seamless Black Membrane Waterproofing Barrier",
        severityLevel = "Severe Risk",
        severityColor = Color(0xFFB71C1C),
        chemicalSpec = "High-Tensile Elastomeric SBS Black Bituminous Membrane",
        description = "Subterranean hydrostatic groundwater penetrating foundation cold joints. Heavy-duty Black Membrane provides complete impervious underground tanking protection against water table pressure.",
        icon = Icons.Default.Foundation,
        defects = listOf(
            DetectedDefect(
                id = "preset_base_1",
                problemTitle = "Negative Hydrostatic Water Infiltration",
                shortLabel = "Basement Leak",
                location = "Subterranean foundation retaining wall",
                severity = "HIGH",
                confidenceScore = 95,
                visualEvidence = "Dark, saturated moisture patterns along subterranean retaining wall joint.",
                likelyCause = "Subterranean groundwater table pressure penetrating foundation cold joints (exact water source requires perimeter excavation review).",
                recommendedAction = "Install continuous seamless black membrane waterproofing barrier with perimeter drainage.",
                fromchemSolution = "Black Membrane (Heavy-Duty Bituminous Tanking Membrane)",
                fromchemProductSpec = "High-Tensile Elastomeric SBS Black Bituminous Membrane",
                boundingBox = DefectBoundingBox(0.18f, 0.12f, 0.82f, 0.88f)
            ),
            DetectedDefect(
                id = "preset_base_2",
                problemTitle = "Cold Joint Groundwater Seepage",
                shortLabel = "Joint Seepage",
                location = "Wall-to-slab base joint",
                severity = "HIGH",
                confidenceScore = 91,
                visualEvidence = "Moisture accumulation and localized pooling along bottom cold joint.",
                likelyCause = "Lack of hydrophilic water-stop or failed external tanking seal.",
                recommendedAction = "Chisel joint, install swellable seal, and tank with bituminous barrier.",
                fromchemSolution = "Black Membrane (Heavy-Duty Bituminous Tanking Membrane)",
                fromchemProductSpec = "Elastomeric Bituminous Tanking System",
                boundingBox = DefectBoundingBox(0.72f, 0.08f, 0.95f, 0.92f)
            )
        )
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

            // 2. Google Gemini AI & Autonomous Computer Vision Analysis Flow
            analysisStatusText = "Autonomous AI checking image quality, relevance and defects..."
            analysisProgress = 0.60f
            val contextDesc = preset?.let { "${it.name} - ${it.category}: ${it.description}" } ?: ""

            val geminiAnalysisRes = GeminiChatService.analyzeLeakPhoto(
                imageBytes = imageBytes,
                bitmap = bitmap,
                contextDescription = contextDesc
            )

            val geminiResult = geminiAnalysisRes.getOrDefault(
                bitmap?.let { GeminiChatService.analyzeBitmapPixels(it, contextDescription = contextDesc) }
                    ?: preset?.let {
                        GeminiLeakAnalysisResult(
                            detectedIssue = it.detectedIssue,
                            recommendedApplication = it.recommendedApplication,
                            suggestedNextStep = it.suggestedNextStep,
                            severityLevel = it.severityLevel,
                            chemicalSpec = it.chemicalSpec,
                            summary = it.description,
                            defectCategory = it.name,
                            confidence = "96% Visual Certainty",
                            defects = it.defects
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

                // Shared Inspection Result Resolution
                val isPresetMode = capturedBitmap == null && selectedImageUri == null && selectedPreset != null && activeGeminiResult == null
                val currentPreset = selectedPreset ?: sampleStructuralPresets[0]
                val displayDefects = if (activeGeminiResult != null) {
                    if (!activeGeminiResult!!.isRelevantForInspection ||
                        activeGeminiResult!!.isImageQualityInsufficient ||
                        !activeGeminiResult!!.hasVisibleDefects
                    ) {
                        emptyList()
                    } else {
                        activeGeminiResult!!.defects
                    }
                } else if (isPresetMode) {
                    currentPreset.defects
                } else {
                    emptyList()
                }

                // Main Display Box
                val infiniteScanTransition = rememberInfiniteTransition(label = "camera_hud_scan")
                val scanLineRatio by infiniteScanTransition.animateFloat(
                    initialValue = 0.05f,
                    targetValue = 0.95f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scan_y"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, FromchemBorder, RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A)),
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
                                text = "Take a picture of wall crack, moisture, ceiling leakage, or dampness",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "AI Camera auto-detects the defect from image pixels",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Camera Viewfinder Reticle & Edge-to-Edge Bounding Boxes

                    if (capturedBitmap != null || selectedPreset != null) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            val strokeW = 3.dp.toPx()
                            val bracketLen = 22.dp.toPx()
                            val bracketColor = if (isAnalyzing) Color(0xFF00E5FF) else Color(0xFF38BDF8).copy(alpha = 0.7f)

                            // Top-Left
                            drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), strokeW)
                            drawLine(bracketColor, Offset(0f, 0f), Offset(0f, bracketLen), strokeW)
                            // Top-Right
                            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), strokeW)
                            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, bracketLen), strokeW)
                            // Bottom-Left
                            drawLine(bracketColor, Offset(0f, size.height), Offset(bracketLen, size.height), strokeW)
                            drawLine(bracketColor, Offset(0f, size.height), Offset(0f, size.height - bracketLen), strokeW)
                            // Bottom-Right
                            drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), strokeW)
                            drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), strokeW)

                            // Scanning Laser Beam Animation when analyzing
                            if (isAnalyzing) {
                                val scanY = size.height * scanLineRatio
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color.Transparent, Color(0xFF00E5FF), Color(0xFF69F0AE), Color(0xFF00E5FF), Color.Transparent)
                                    ),
                                    start = Offset(0f, scanY),
                                    end = Offset(size.width, scanY),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }

                        // Edge-to-Edge Bounding Boxes for Detected Defects
                        if (!isAnalyzing && displayDefects.isNotEmpty()) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                displayDefects.forEachIndexed { idx, defect ->
                                    defect.boundingBox?.let { box ->
                                        val left = (box.xMin * w).coerceIn(0f, w)
                                        val top = (box.yMin * h).coerceIn(0f, h)
                                        val right = (box.xMax * w).coerceIn(left + 20f, w)
                                        val bottom = (box.yMax * h).coerceIn(top + 20f, h)
                                        val boxW = right - left
                                        val boxH = bottom - top

                                        val color = when (defect.severity.uppercase()) {
                                            "HIGH" -> Color(0xFFFF1744)
                                            "LOW" -> Color(0xFF00E676)
                                            else -> Color(0xFFFF9100)
                                        }

                                        // Semi-transparent defect zone shading
                                        drawRect(
                                            color = color.copy(alpha = 0.16f),
                                            topLeft = Offset(left, top),
                                            size = Size(boxW, boxH)
                                        )

                                        // Defect bounding perimeter stroke
                                        drawRect(
                                            color = color,
                                            topLeft = Offset(left, top),
                                            size = Size(boxW, boxH),
                                            style = Stroke(width = 2.5f.dp.toPx())
                                        )

                                        // Corner accent markers on bounding box
                                        val cLen = 10.dp.toPx().coerceAtMost(boxW / 3f).coerceAtMost(boxH / 3f)
                                        val cStroke = 3.5f.dp.toPx()
                                        drawLine(color, Offset(left, top), Offset(left + cLen, top), cStroke)
                                        drawLine(color, Offset(left, top), Offset(left, top + cLen), cStroke)
                                        drawLine(color, Offset(right, top), Offset(right - cLen, top), cStroke)
                                        drawLine(color, Offset(right, top), Offset(right, top + cLen), cStroke)
                                        drawLine(color, Offset(left, bottom), Offset(left + cLen, bottom), cStroke)
                                        drawLine(color, Offset(left, bottom), Offset(left, bottom - cLen), cStroke)
                                        drawLine(color, Offset(right, bottom), Offset(right - cLen, bottom), cStroke)
                                        drawLine(color, Offset(right, bottom), Offset(right, bottom - cLen), cStroke)
                                    }
                                }
                            }
                        }

                        // Autonomous Result Overlays on the Viewfinder
                        if (!isAnalyzing && (activeGeminiResult != null || isPresetMode)) {
                            val isIrrelevant = activeGeminiResult?.isRelevantForInspection == false
                            val isInsufficient = activeGeminiResult?.isImageQualityInsufficient == true
                            val isClean = activeGeminiResult?.isRelevantForInspection == true && activeGeminiResult?.hasVisibleDefects == false

                            // Top Banner: Status & Defect Detection Count
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Black.copy(alpha = 0.88f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when {
                                            isIrrelevant -> Color(0xFFFF5252)
                                            isInsufficient -> Color(0xFFFFB74D)
                                            isClean -> Color(0xFF69F0AE)
                                            else -> Color(0xFF00E5FF).copy(alpha = 0.8f)
                                        }
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = when {
                                                isIrrelevant -> Color(0xFFFF5252)
                                                isInsufficient -> Color(0xFFFFB74D)
                                                isClean -> Color(0xFF69F0AE)
                                                else -> Color(0xFF00E5FF)
                                            },
                                            modifier = Modifier.size(7.dp)
                                        ) {}
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = when {
                                                isIrrelevant -> "IMAGE NOT SUITABLE FOR LEAK DETECTION"
                                                isInsufficient -> "IMAGE QUALITY INSUFFICIENT"
                                                isClean -> "NO OBVIOUS VISIBLE DEFECT DETECTED"
                                                else -> "EDGE-TO-EDGE SCAN: ${displayDefects.size} DEFECT${if (displayDefects.size > 1) "S" else ""} FOUND"
                                            },
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (!isIrrelevant && !isInsufficient) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = activeGeminiResult?.confidence ?: "96% Certainty",
                                                color = Color(0xFF69F0AE),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Pill
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF0A2540).copy(alpha = 0.92f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isIrrelevant) Color(0xFFFF5252) else Color(0xFF38BDF8)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isIrrelevant -> Icons.Default.Block
                                                isInsufficient -> Icons.Default.Warning
                                                isClean -> Icons.Default.CheckCircle
                                                else -> Icons.Default.CheckCircle
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isIrrelevant -> Color(0xFFFF5252)
                                                isInsufficient -> Color(0xFFFFB74D)
                                                else -> Color(0xFF69F0AE)
                                            },
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        val bottomText = when {
                                            isIrrelevant -> "Detected Object: ${activeGeminiResult?.detectedObject ?: "Unrelated Object"}"
                                            isInsufficient -> "Please capture a clearer, closer image"
                                            isClean -> "Clean Surface: ${activeGeminiResult?.detectedSurface ?: "Wall"} (Intact)"
                                            else -> {
                                                val primarySol = displayDefects.firstOrNull()?.fromchemSolution
                                                    ?: activeGeminiResult?.recommendedApplication
                                                    ?: currentPreset.recommendedApplication
                                                "Solution: $primarySol"
                                            }
                                        }
                                        Text(
                                            text = bottomText,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Scanning Overlay Effect when analyzing
                    if (isAnalyzing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.72f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { analysisProgress },
                                    color = Color(0xFF00E5FF),
                                    trackColor = Color.Gray,
                                    modifier = Modifier.size(46.dp)
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
                                    text = "Edge-to-edge inspection: Wall Cracks • Moisture • Ceiling Leaks • Damp Walls • Joints",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Diagnostic Result View
                val currentCategory = activeGeminiResult?.defectCategory ?: currentPreset.name
                val currentDetectedIssue = displayDefects.firstOrNull()?.problemTitle
                    ?: activeGeminiResult?.detectedIssue
                    ?: currentPreset.detectedIssue
                val currentRecommendedApplication = displayDefects.firstOrNull()?.fromchemSolution
                    ?: activeGeminiResult?.recommendedApplication
                    ?: currentPreset.recommendedApplication
                val currentSuggestedNextStep = displayDefects.firstOrNull()?.recommendedAction
                    ?: activeGeminiResult?.suggestedNextStep
                    ?: currentPreset.suggestedNextStep
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
                    // Firebase Sync Status indicator
                    if (firebaseSyncStatus != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (firebaseDocId != null) Color(0xFFE8F5E9) else FromchemSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
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

                    if (activeGeminiResult?.isRelevantForInspection == false) {
                        // =========================================================================
                        // STATE 1: INVALID / IRRELEVANT IMAGE (Section 14 of Specification)
                        // Bottle, person, car, phone, food, etc.
                        // DO NOT show leakage diagnosis, cracks, or FromChem treatments.
                        // =========================================================================
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDA4AF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("irrelevant_image_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFE11D48),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Block,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "IMAGE NOT SUITABLE FOR LEAK DETECTION",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF9F1239),
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Relevance Validation Check Failed",
                                            fontSize = 11.sp,
                                            color = Color(0xFFBE123C)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFFFECDD3))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Detected Object
                                Text(
                                    text = "Detected Object:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF881337)
                                )
                                Text(
                                    text = activeGeminiResult?.detectedObject ?: "Bottle / Non-construction Object",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239),
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Reason
                                Text(
                                    text = "Reason:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF881337)
                                )
                                Text(
                                    text = activeGeminiResult?.irrelevanceReason
                                        ?: "The uploaded image does not appear to show a building surface or visible waterproofing/construction defect.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4C0519),
                                    modifier = Modifier.padding(top = 2.dp),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Please capture
                                Text(
                                    text = "Please capture:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF881337)
                                )
                                Text(
                                    text = activeGeminiResult?.guidanceMessage
                                        ?: "A clear photo of the wall, ceiling, roof, terrace, floor, bathroom, pipe area, or other suspected defective area.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4C0519),
                                    modifier = Modifier.padding(top = 2.dp),
                                    lineHeight = 16.sp
                                )

                                // Optional OCR Support
                                if (!activeGeminiResult?.ocrDetectedText.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.85f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.TextFields,
                                                contentDescription = null,
                                                tint = Color(0xFF9F1239),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Detected Text (OCR):",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF881337)
                                                )
                                                Text(
                                                    text = activeGeminiResult!!.ocrDetectedText!!,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF4C0519)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { cameraLauncher.launch() },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retake Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { galleryLauncher.launch("image/*") },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9F1239)),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE11D48))
                                        )
                                    ) {
                                        Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Upload Surface", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else if (activeGeminiResult?.isImageQualityInsufficient == true) {
                        // =========================================================================
                        // STATE 2: IMAGE QUALITY INSUFFICIENT (Section 4 of Specification)
                        // Blurry, too dark, obstructed, inadequate resolution
                        // =========================================================================
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("insufficient_quality_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFD97706),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "IMAGE QUALITY INSUFFICIENT",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                        Text(
                                            text = "Please capture a clearer and closer image",
                                            fontSize = 11.sp,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = activeGeminiResult?.imageQualityMessage?.ifBlank { null }
                                        ?: "Please capture a clearer and closer image of the suspected defective area.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Capture Recommendations:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                val tips = activeGeminiResult?.suggestedAdditionalImages?.ifEmpty { null } ?: listOf(
                                    "Ensure adequate light on the inspected wall/ceiling area",
                                    "Center the camera closely on the suspected defect",
                                    "Hold camera steady to avoid blur",
                                    "Capture at a straight angle without glare or reflections"
                                )
                                tips.forEach { tip ->
                                    Text(
                                        text = "• $tip",
                                        fontSize = 11.sp,
                                        color = Color(0xFF78350F),
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { cameraLauncher.launch() },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retake Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { galleryLauncher.launch("image/*") },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF92400E)),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD97706))
                                        )
                                    ) {
                                        Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Upload Clear Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else if (activeGeminiResult?.isRelevantForInspection == true && activeGeminiResult?.hasVisibleDefects == false) {
                        // =========================================================================
                        // STATE 3: CLEAN SURFACE / NO OBVIOUS VISIBLE DEFECT (Section 7)
                        // Do not force a diagnosis when substrate is intact.
                        // =========================================================================
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clean_surface_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Analytics, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "AI VISUAL INSPECTION REPORT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFDCFCE7)) {
                                        Text(
                                            text = "NO DEFECT VISIBLE",
                                            color = Color(0xFF15803D),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Detected Surface
                                Surface(shape = RoundedCornerShape(8.dp), color = Color.White) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Detected Surface: ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = activeGeminiResult?.detectedSurface ?: "Wall / Building Surface",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF14532D)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "No obvious visible defect detected.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF14532D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "The inspected surface appears clean, structurally sound, and shows no visible signs of active water seepage, cracking, efflorescence, or substrate deterioration. Regular periodic monitoring is recommended.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 16.sp
                                )

                                if (!activeGeminiResult?.ocrDetectedText.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Detected Text: ${activeGeminiResult!!.ocrDetectedText}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { cameraLauncher.launch() },
                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Inspect Another Area", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // =========================================================================
                        // STATE 4: GENUINE VISIBLE DEFECTS / STRUCTURED INSPECTION REPORT
                        // =========================================================================

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
                                    text = "AI VISUAL INSPECTION REPORT",
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Detected Surface (Section 13)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FromchemSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Detected Surface: ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemTextPrimary
                                )
                                Text(
                                    text = activeGeminiResult?.detectedSurface ?: currentPreset.category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FromchemPrimary
                                )
                            }
                        }

                        // Optional OCR Support (Section 8)
                        if (!activeGeminiResult?.ocrDetectedText.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEFF6FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFields,
                                        contentDescription = null,
                                        tint = Color(0xFF1D4ED8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Detected Text: ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                    Text(
                                        text = activeGeminiResult!!.ocrDetectedText!!,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    // Inspection Summary Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when {
                                            currentCategory.contains("Crack", ignoreCase = true) -> Icons.Default.BrokenImage
                                            currentCategory.contains("Ceiling", ignoreCase = true) || currentCategory.contains("Leak", ignoreCase = true) -> Icons.Default.WaterDrop
                                            currentCategory.contains("Basement", ignoreCase = true) -> Icons.Default.Foundation
                                            else -> Icons.Default.Opacity
                                        },
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "EDGE-TO-EDGE INSPECTION",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = FromchemAccentGreen
                                    ) {
                                        Text(
                                            text = "${displayDefects.size} DEFECT${if (displayDefects.size > 1) "S" else ""}",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentDetectedIssue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentSummary,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // All Detected Defects Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ALL DETECTED DEFECTS (${displayDefects.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FromchemTextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Edge-to-Edge Analysis",
                            fontSize = 10.sp,
                            color = FromchemTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // List of Each Detected Defect Card
                    displayDefects.forEachIndexed { index, defect ->
                        val defectColor = when (defect.severity.uppercase()) {
                            "HIGH" -> Color(0xFFD32F2F)
                            "LOW" -> Color(0xFF2E7D32)
                            else -> Color(0xFFF57C00)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, defectColor.copy(alpha = 0.35f)),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Defect Card Header
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
                                            shape = RoundedCornerShape(6.dp),
                                            color = defectColor.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "#${index + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = defectColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. Defect: ${defect.problemTitle}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FromchemTextPrimary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = defectColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "3. Severity: ${defect.severity.uppercase()}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = defectColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Defect Location & Confidence
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = FromchemPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "2. Location: ${defect.location}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FromchemTextSecondary
                                        )
                                    }

                                    Text(
                                        text = "4. Confidence: ${defect.confidenceScore}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FromchemAccentGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = FromchemBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Visual Evidence Observed
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "5. Visual Evidence:",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0284C7)
                                        )
                                        Text(
                                            text = defect.visualEvidence,
                                            fontSize = 11.sp,
                                            color = FromchemTextPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Likely Cause (Clearly labeled as likely vs confirmed)
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFF57C00),
                                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "6. Possible Cause (Hidden origin unconfirmed):",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF57C00)
                                        )
                                        Text(
                                            text = defect.likelyCause,
                                            fontSize = 11.sp,
                                            color = FromchemTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Practical Recommended Action
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = FromchemAccentGreen,
                                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "7. Recommended Action:",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FromchemAccentGreen
                                        )
                                        Text(
                                            text = defect.recommendedAction,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = FromchemTextPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Matched FromChem Chemical Solution
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = FromchemPrimaryContainer.copy(alpha = 0.55f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.25f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Science,
                                            contentDescription = null,
                                            tint = FromchemPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "8. RECOMMENDED FROMCHEM SOLUTION",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FromchemPrimary,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = defect.fromchemSolution,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FromchemPrimary
                                            )
                                            if (defect.fromchemProductSpec.isNotBlank()) {
                                                Text(
                                                    text = defect.fromchemProductSpec,
                                                    fontSize = 10.sp,
                                                    color = FromchemTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Safety & Technical Limitation Note
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp).padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeGeminiResult?.safetyLimitationNote
                                    ?: "AI is an automated visual inspection assistant, not a licensed structural engineer. Professional on-site physical inspection is recommended for major structural defects or hidden moisture paths. An image alone cannot confirm the exact origin of water ingress.",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 14.sp
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

