package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.DetectedDefect
import com.example.ai.GeminiChatService
import com.example.ai.GeminiLeakAnalysisResult
import com.example.data.FirebasePhotoManager
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AI Leak Detector Dialog powered by the exact ChatGPT Camera experience.
 *
 * Flow:
 * 1. Fullscreen ChatGPT Camera interface with live viewfinder, reticle brackets,
 *    tap-to-focus, 1x/2x zoom, and the signature ChatGPT double-ring shutter button.
 * 2. Instant review screen with defect bounding box overlays, strict defect detection pipeline,
 *    and FromChem solution matching.
 * 3. ChatGPT Interactive Chat Dock: user can ask follow-up questions about the photo,
 *    with a 1-tap Retake/New Scan button to jump back into the camera at any time.
 */
@Composable
fun AILeakDetectorDialog(
    onDismiss: () -> Unit,
    onRequestQuoteForSolution: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<GeminiLeakAnalysisResult?>(null) }
    var cloudUploadStatus by remember { mutableStateOf<String?>(null) }
    var activeScenarioLabel by remember { mutableStateOf<String?>(null) }

    fun processBitmap(bitmap: Bitmap, contextNote: String) {
        activeBitmap = bitmap
        activeScenarioLabel = contextNote
        isAnalyzing = true
        analysisResult = null
        cloudUploadStatus = null

        coroutineScope.launch {
            val bytes = FirebasePhotoManager.bitmapToByteArray(bitmap)

            // Step 1: Run Strict AI Leak Detection Pipeline
            val result = GeminiChatService.analyzeLeakPhoto(
                imageBytes = bytes,
                bitmap = bitmap,
                contextDescription = contextNote
            ).getOrElse {
                GeminiChatService.getDefaultLeakAnalysis(contextNote)
            }

            analysisResult = result
            isAnalyzing = false

            // Step 2: Upload to Firebase Storage and Cloud Firestore if available
            withContext(Dispatchers.IO) {
                if (FirebasePhotoManager.isFirebaseInitialized(context)) {
                    val uploadResult = FirebasePhotoManager.uploadImageToStorage(
                        context = context,
                        imageBytes = bytes,
                        rawFileName = "leak_scan.jpg"
                    )
                    uploadResult.onSuccess { (downloadUrl, storagePath) ->
                        cloudUploadStatus = "Cloud Synced"
                        FirebasePhotoManager.savePhotoRecordToFirestore(
                            context = context,
                            imageUrl = downloadUrl,
                            fileName = "leak_scan.jpg",
                            storagePath = storagePath,
                            geminiResult = result
                        )
                    }.onFailure {
                        cloudUploadStatus = "Saved Locally"
                    }
                } else {
                    cloudUploadStatus = "Local Inspection Mode"
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        if (activeBitmap == null) {
            // =========================================================
            // EXACT CHATGPT CAMERA FEATURE
            // =========================================================
            ChatGPTCameraView(
                onPhotoCaptured = { bitmap, contextNote ->
                    processBitmap(bitmap, contextNote)
                },
                onClose = onDismiss,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // =========================================================
            // CHATGPT POST-CAPTURE & ANALYSIS REVIEW SCREEN
            // =========================================================
            ChatGPTReviewScreen(
                bitmap = activeBitmap!!,
                isAnalyzing = isAnalyzing,
                analysisResult = analysisResult,
                cloudUploadStatus = cloudUploadStatus,
                onRetake = {
                    activeBitmap = null
                    analysisResult = null
                    isAnalyzing = false
                },
                onClose = onDismiss,
                onRequestQuoteForSolution = onRequestQuoteForSolution
            )
        }
    }
}

/**
 * ChatGPT-style Post-Capture Inspection Screen with defect overlays,
 * AI diagnosis cards, and interactive ChatGPT chat dock.
 */
@Composable
private fun ChatGPTReviewScreen(
    bitmap: Bitmap,
    isAnalyzing: Boolean,
    analysisResult: GeminiLeakAnalysisResult?,
    cloudUploadStatus: String?,
    onRetake: () -> Unit,
    onClose: () -> Unit,
    onRequestQuoteForSolution: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userQuestionText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isAnsweringQuestion by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_leak_detector_dialog"),
        color = Color(0xFF0F172A) // Sleek dark slate aesthetic
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Retake Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.clickable { onRetake() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retake Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Retake",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Center Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF10A37F),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "ChatGPT Inspection",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Close Button
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onClose() }
                        .testTag("close_leak_detector_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Captured Photo with Defect Bounding Box Overlays
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Black,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Surface",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        // Defect Bounding Box Canvas Overlay
                        analysisResult?.let { res ->
                            if (res.isRelevantForInspection && res.hasVisibleDefects && res.defects.isNotEmpty()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    res.defects.forEach { defect ->
                                        val box = defect.boundingBox
                                        if (box != null) {
                                            val left = box.xMin * size.width
                                            val top = box.yMin * size.height
                                            val width = (box.xMax - box.xMin) * size.width
                                            val height = (box.yMax - box.yMin) * size.height

                                            drawRect(
                                                color = Color(0xFFEF4444),
                                                topLeft = Offset(left, top),
                                                size = Size(width, height),
                                                style = Stroke(width = 3.dp.toPx())
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Analyzing Overlay
                        if (isAnalyzing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.65f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF10A37F),
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Text(
                                        text = "ChatGPT Vision is analyzing defects...",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Status Badge
                        cloudUploadStatus?.let { status ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = status,
                                    color = if (status.contains("Synced")) Color(0xFF4ADE80) else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Inspection Report Results
                if (analysisResult != null && !isAnalyzing) {
                    val result = analysisResult

                    when {
                        result.isImageQualityInsufficient -> {
                            QualityInsufficientCard(result = result)
                        }
                        !result.isRelevantForInspection -> {
                            IrrelevantImageCard(result = result)
                        }
                        !result.hasVisibleDefects || result.defects.isEmpty() -> {
                            CleanSurfaceCard(result = result)
                        }
                        else -> {
                            DefectReportCard(
                                result = result,
                                onRequestQuote = { product, defectTitle ->
                                    onRequestQuoteForSolution(product, defectTitle)
                                    onClose()
                                }
                            )
                        }
                    }
                }

                // Interactive ChatGPT Q&A History
                if (chatMessages.isNotEmpty()) {
                    Text(
                        text = "ChatGPT Conversation",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    chatMessages.forEach { (question, answer) ->
                        // User message
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "You: $question",
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // ChatGPT response
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0F241F),
                            border = BorderStroke(1.dp, Color(0xFF10A37F).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF10A37F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = answer,
                                    color = Color(0xFFD1FAE5),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                if (isAnsweringQuestion) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF10A37F),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "ChatGPT is typing...",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom ChatGPT Prompt & Action Dock
            Surface(
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ChatGPT Prompt Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = userQuestionText,
                            onValueChange = { userQuestionText = it },
                            placeholder = {
                                Text(
                                    text = "Ask ChatGPT about this photo...",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 13.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFF10A37F),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        // Iconic Circular Send Button
                        Surface(
                            shape = CircleShape,
                            color = if (userQuestionText.isNotBlank()) Color.White else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable(enabled = userQuestionText.isNotBlank() && !isAnsweringQuestion) {
                                    val question = userQuestionText.trim()
                                    userQuestionText = ""
                                    isAnsweringQuestion = true

                                    coroutineScope.launch {
                                        val prompt = "Surface Inspection Context: ${analysisResult?.summary ?: "Building inspection"}\nUser Question: $question"
                                        val result = GeminiChatService.sendMessage(
                                            history = emptyList(),
                                            userMessage = "$prompt: $question"
                                        )
                                        val reply = result.getOrElse {
                                            "For this question, we recommend checking the defect report above or requesting a free technical inspection with FromChem engineers."
                                        }
                                        chatMessages = chatMessages + (question to reply)
                                        isAnsweringQuestion = false
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (userQuestionText.isNotBlank()) Color.Black else Color(0xFF475569),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Quick Actions (Snap Another Photo)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onRetake,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF38BDF8))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Snap Another Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (analysisResult?.isRelevantForInspection == true && analysisResult.hasVisibleDefects) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "${analysisResult.defects.size} Issue(s) Detected",
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityInsufficientCard(result: GeminiLeakAnalysisResult) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFBEB),
        border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
        modifier = Modifier.fillMaxWidth().testTag("quality_insufficient_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "IMAGE QUALITY INSUFFICIENT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF92400E)
                )
            }

            Text(
                text = result.imageQualityMessage.ifBlank { "Please capture a clearer and closer image of the suspected defective area." },
                fontSize = 13.sp,
                color = Color(0xFF78350F),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun IrrelevantImageCard(result: GeminiLeakAnalysisResult) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.5.dp, Color(0xFF94A3B8)),
        modifier = Modifier.fillMaxWidth().testTag("irrelevant_image_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "IMAGE NOT SUITABLE FOR LEAK DETECTION",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "DETECTED OBJECT:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = result.detectedObject ?: "Unrelated Object (e.g. Bottle)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "REASON:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = result.irrelevanceReason ?: "The image does not show a relevant building/construction surface or visible waterproofing defect.",
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "PLEASE CAPTURE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemPrimary
                )
                Text(
                    text = "A clear photo of the wall, ceiling, roof, terrace, floor, bathroom, pipe area, or suspected leakage/dampness area.",
                    fontSize = 12.sp,
                    color = FromchemTextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun CleanSurfaceCard(result: GeminiLeakAnalysisResult) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.5.dp, FromchemAccentGreen),
        modifier = Modifier.fillMaxWidth().testTag("clean_surface_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FromchemAccentGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "AI INSPECTION REPORT - CLEAN SURFACE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF166534)
                )
            }

            Text(
                text = "Detected Surface: ${result.detectedSurface}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF14532D)
            )

            Text(
                text = result.noDefectMessage ?: "No obvious visible defect detected.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF15803D)
            )

            Text(
                text = "The inspected surface appears clean and structurally intact with no signs of active water ingress, cracks, efflorescence, or peeling. Regular preventative maintenance recommended.",
                fontSize = 12.sp,
                color = Color(0xFF166534),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun DefectReportCard(
    result: GeminiLeakAnalysisResult,
    onRequestQuote: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("defect_report_card"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Header Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI INSPECTION REPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FromchemPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Detected Surface: ${result.detectedSurface}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEF4444)
                ) {
                    Text(
                        text = "${result.defects.size} DEFECT(S) DETECTED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Optional OCR Detected Text
        if (!result.ocrDetectedText.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = FromchemPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Detected Text: \"${result.ocrDetectedText}\"",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                }
            }
        }

        // List each detected defect in sequence
        result.defects.forEachIndexed { index, defect ->
            DefectDetailCard(
                index = index + 1,
                defect = defect,
                onRequestQuote = { product ->
                    onRequestQuote(product, defect.problemTitle)
                }
            )
        }
    }
}

@Composable
private fun DefectDetailCard(
    index: Int,
    defect: DetectedDefect,
    onRequestQuote: (String) -> Unit
) {
    val severityColor = when (defect.severity.uppercase()) {
        "HIGH" -> Color(0xFFEF4444)
        "MODERATE" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, severityColor.copy(alpha = 0.5f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Defect Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = severityColor,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$index",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = defect.problemTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = severityColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${defect.severity} SEVERITY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Location & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = defect.location,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${defect.confidenceScore}% Visual Confidence",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemPrimary
                )
            }

            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)

            // Visual Evidence
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "VISUAL EVIDENCE:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = defect.visualEvidence,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B),
                    lineHeight = 16.sp
                )
            }

            // Likely Cause
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "POSSIBLE CAUSE:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = defect.likelyCause,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }

            // Recommended Action
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "RECOMMENDED ACTION:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = defect.recommendedAction,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B),
                    lineHeight = 16.sp
                )
            }

            // FromChem Matched Solution Card
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = FromchemPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "RECOMMENDED FROMCHEM SOLUTION:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FromchemPrimary
                        )
                    }

                    Text(
                        text = defect.fromchemSolution,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = defect.fromchemProductSpec,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { onRequestQuote(defect.fromchemSolution) },
                        colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RequestQuote,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Request Quote for this Solution",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
