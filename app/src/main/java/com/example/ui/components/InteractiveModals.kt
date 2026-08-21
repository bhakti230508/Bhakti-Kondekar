package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ChatMessageItem
import com.example.ai.GeminiChatService
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetQuoteDialog(
    onDismiss: () -> Unit,
    onSubmitQuote: (String, String, String) -> Unit
) {
    var selectedType by remember { mutableStateOf("Roofing Systems") }
    var areaSqFt by remember { mutableStateOf("2500") }
    var contactInfo by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (!submitted) {
                Button(
                    onClick = {
                        submitted = true
                        onSubmitQuote(selectedType, areaSqFt, contactInfo)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Submit Quote Request", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (!submitted) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = FromchemTextSecondary)
                }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = FromchemPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (submitted) "Quote Request Received" else "Instant Waterproofing Quote",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = FromchemTextPrimary
                )
            }
        },
        text = {
            if (!submitted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Select your project specification and area size to receive an instant chemical solution breakdown.",
                        fontSize = 12.sp,
                        color = FromchemTextSecondary
                    )

                    Text(
                        text = "Project Spec:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Roofing", "Basement", "Industrial").forEach { type ->
                            val isSel = selectedType.startsWith(type)
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedType = "$type Systems" },
                                label = { Text(type, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FromchemPrimaryContainer,
                                    selectedLabelColor = FromchemPrimary
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = areaSqFt,
                        onValueChange = { areaSqFt = it },
                        label = { Text("Estimated Area (sq ft)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = contactInfo,
                        onValueChange = { contactInfo = it },
                        label = { Text("Email or Phone Number") },
                        placeholder = { Text("e.g. contact@building.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    val estArea = areaSqFt.toDoubleOrNull() ?: 2500.0
                    val estCostMin = (estArea * 35).toInt()
                    val estCostMax = (estArea * 85).toInt()

                    Card(
                        colors = CardDefaults.cardColors(containerColor = FromchemPrimaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Estimated Material & Application Range:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                            Text(
                                text = "₹${"%,d".format(estCostMin)} - ₹${"%,d".format(estCostMax)} (ISO Certified Crystalline Seal)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FromchemTextPrimary
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FromchemAccentGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Thank you! A Fromchem Chemical Engineer will review your $selectedType specification ($areaSqFt sq ft) and reach out to $contactInfo with a formal technical proposal within 2 hours.",
                        fontSize = 13.sp,
                        color = FromchemTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

data class ChatMessage(val sender: String, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveChatDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessageItem(
                    sender = "ai",
                    text = "Hello! Welcome to Fromchem Technical Support. I am your AI Waterproofing Engineer. You can chat with me using text or voice. How can I assist you with your project today?"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isVoiceModeActive by remember { mutableStateOf(false) }
    var autoSpeakResponses by remember { mutableStateOf(true) }
    var isListening by remember { mutableStateOf(false) }
    var currentlySpeakingText by remember { mutableStateOf<String?>(null) }
    var lastVoiceTranscript by remember { mutableStateOf<String?>(null) }

    // Text To Speech initialization
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale.US
                ttsEngine?.setSpeechRate(0.95f)
            }
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                currentlySpeakingText = utteranceId
            }

            override fun onDone(utteranceId: String?) {
                currentlySpeakingText = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                currentlySpeakingText = null
            }
        })
        ttsEngine = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speakText(textToSpeak: String) {
        ttsEngine?.let { tts ->
            tts.stop()
            currentlySpeakingText = textToSpeak
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, textToSpeak.take(30))
        }
    }

    fun stopSpeaking() {
        ttsEngine?.stop()
        currentlySpeakingText = null
    }

    fun sendQuery(userText: String, fromVoice: Boolean = false) {
        if (userText.isBlank() || isLoading) return
        stopSpeaking()
        val userMsg = ChatMessageItem(sender = "user", text = userText)
        val updatedHistory = messages + userMsg
        messages = updatedHistory
        inputText = ""
        lastVoiceTranscript = userText
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem((updatedHistory.size - 1).coerceAtLeast(0))
            val result = GeminiChatService.sendMessage(updatedHistory, userText)
            val aiResponse = result.getOrDefault("I'm sorry, I encountered an issue processing that request. Please try again or tap 'Get Quote' for direct assistance.")
            val aiMsg = ChatMessageItem(sender = "ai", text = aiResponse)
            val finalHistory = updatedHistory + aiMsg
            messages = finalHistory
            isLoading = false
            listState.animateScrollToItem((finalHistory.size - 1).coerceAtLeast(0))

            if (autoSpeakResponses || fromVoice || isVoiceModeActive) {
                speakText(aiResponse)
            }
        }
    }

    // Android Speech Recognizer Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenTextList?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                sendQuery(spokenText, fromVoice = true)
            }
        }
    }

    fun startListening() {
        stopSpeaking()
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your waterproofing query to Fromchem AI...")
            }
            isListening = true
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            isListening = false
            Toast.makeText(context, "Speech recognition not available on this device. You can choose a sample voice query or type below.", Toast.LENGTH_LONG).show()
        }
    }

    val suggestedQuestions = listOf(
        "What is Crystalline Waterproofing?",
        "How fast does Polyurea cure?",
        "Best waterproofing for basements?",
        "How does the 15-year warranty work?"
    )

    val sampleVoiceQueries = listOf(
        "How to treat damp wall cracks?",
        "Best chemical coat for roof terrace?",
        "Basement water leakage solution",
        "Explain crystalline self-healing technology"
    )

    AlertDialog(
        onDismissRequest = {
            stopSpeaking()
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    stopSpeaking()
                    onDismiss()
                },
                modifier = Modifier.testTag("chat_close_button")
            ) {
                Text("Close", color = FromchemTextSecondary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Auto-speak toggle
                IconButton(
                    onClick = {
                        autoSpeakResponses = !autoSpeakResponses
                        if (!autoSpeakResponses) stopSpeaking()
                        Toast.makeText(context, if (autoSpeakResponses) "Voice speech output: ON" else "Voice speech output: MUTED", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("chat_tts_toggle_button")
                ) {
                    Icon(
                        imageVector = if (autoSpeakResponses) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Speech Output",
                        tint = if (autoSpeakResponses) FromchemPrimary else FromchemTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (messages.size > 1) {
                    IconButton(
                        onClick = {
                            stopSpeaking()
                            messages = listOf(
                                ChatMessageItem(
                                    sender = "ai",
                                    text = "Chat history cleared. How can I assist you with your waterproofing project?"
                                )
                            )
                            lastVoiceTranscript = null
                        },
                        modifier = Modifier.testTag("chat_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Chat",
                            tint = FromchemTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FromchemPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVoiceModeActive) Icons.Default.Mic else Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = FromchemPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isVoiceModeActive) "AI Voice Assistant" else "Fromchem Support Chat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = FromchemTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (currentlySpeakingText != null) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = FromchemAccentGreen
                                    ) {
                                        Text(
                                            text = "SPEAKING",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(FromchemAccentGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isVoiceModeActive) "Voice Interactive • Powered by Gemini" else "Active • Gemini 3.5 Flash",
                                    fontSize = 10.sp,
                                    color = FromchemTextSecondary
                                )
                            }
                        }
                    }

                    // Mode Switcher Pill (Text vs Voice)
                    Surface(
                        onClick = {
                            isVoiceModeActive = !isVoiceModeActive
                            if (!isVoiceModeActive) stopSpeaking()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isVoiceModeActive) FromchemPrimary else FromchemPrimaryContainer,
                        modifier = Modifier.testTag("voice_mode_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVoiceModeActive) Icons.Default.Chat else Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (isVoiceModeActive) Color.White else FromchemPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isVoiceModeActive) "Text Mode" else "Voice Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVoiceModeActive) Color.White else FromchemPrimary
                            )
                        }
                    }
                }
            }
        },
        text = {
            if (isVoiceModeActive) {
                // Dedicated Voice Assistant Layout
                VoiceAssistantModeView(
                    messages = messages,
                    isLoading = isLoading,
                    isListening = isListening,
                    isSpeaking = currentlySpeakingText != null,
                    lastVoiceTranscript = lastVoiceTranscript,
                    sampleVoiceQueries = sampleVoiceQueries,
                    onStartListening = { startListening() },
                    onStopSpeaking = { stopSpeaking() },
                    onSelectSampleVoiceQuery = { sampleQuery ->
                        sendQuery(sampleQuery, fromVoice = true)
                    }
                )
            } else {
                // Standard Text Chat View with Microphone Integration
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(370.dp)
                ) {
                    // Messages List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { msg ->
                            val isUser = msg.sender == "user"
                            val isThisSpeaking = currentlySpeakingText != null && msg.text.startsWith(currentlySpeakingText?.take(20) ?: "")
                            
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    if (!isUser) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(FromchemPrimaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = FromchemPrimary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }

                                    Surface(
                                        modifier = Modifier.widthIn(max = 250.dp),
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        ),
                                        color = if (isUser) FromchemPrimary else FromchemPrimaryContainer,
                                        shadowElevation = 1.dp
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = msg.text,
                                                fontSize = 12.sp,
                                                color = if (isUser) Color.White else FromchemTextPrimary,
                                                lineHeight = 17.sp
                                            )

                                            // Text-to-Speech Listen Action for AI messages
                                            if (!isUser) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            if (isThisSpeaking) {
                                                                stopSpeaking()
                                                            } else {
                                                                speakText(msg.text)
                                                            }
                                                        }
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isThisSpeaking) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                                                        contentDescription = if (isThisSpeaking) "Stop reading" else "Listen to message",
                                                        tint = if (isThisSpeaking) Color(0xFFD32F2F) else FromchemPrimary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (isThisSpeaking) "Stop Audio" else "Listen",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isThisSpeaking) Color(0xFFD32F2F) else FromchemPrimary
                                                    )
                                                    if (isThisSpeaking) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        LiveVoiceSoundBars(color = Color(0xFFD32F2F))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (isLoading) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(start = 30.dp, top = 4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = FromchemPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Analyzing chemical compounds...",
                                        fontSize = 11.sp,
                                        color = FromchemTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggested Prompts Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(suggestedQuestions) { prompt ->
                            SuggestionChip(
                                onClick = { sendQuery(prompt) },
                                label = { Text(prompt, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = FromchemSurfaceVariant,
                                    labelColor = FromchemPrimary
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = FromchemBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Box with Voice Mic Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Type or tap 🎙️ to speak...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(20.dp),
                            enabled = !isLoading,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FromchemPrimary,
                                unfocusedBorderColor = FromchemBorder
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Microphone Voice Input Button
                        IconButton(
                            onClick = { startListening() },
                            enabled = !isLoading,
                            modifier = Modifier
                                .testTag("chat_voice_input_button")
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isListening) Color(0xFFD32F2F) else FromchemPrimaryContainer)
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = if (isListening) Color.White else FromchemPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Send Button
                        IconButton(
                            onClick = { sendQuery(inputText) },
                            enabled = inputText.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .testTag("chat_send_button")
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank() && !isLoading) FromchemPrimary else FromchemBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Message",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun VoiceAssistantModeView(
    messages: List<ChatMessageItem>,
    isLoading: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    lastVoiceTranscript: String?,
    sampleVoiceQueries: List<String>,
    onStartListening: () -> Unit,
    onStopSpeaking: () -> Unit,
    onSelectSampleVoiceQuery: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening || isSpeaking || isLoading) 1.22f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val lastAiMessage = messages.lastOrNull { it.sender == "ai" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Status indicator banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
                isListening -> Color(0xFFFFEBEE)
                isSpeaking -> Color(0xFFE8F5E9)
                isLoading -> FromchemPrimaryContainer
                else -> FromchemSurfaceVariant
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = when {
                        isListening -> Icons.Default.Mic
                        isSpeaking -> Icons.Default.VolumeUp
                        isLoading -> Icons.Default.Refresh
                        else -> Icons.Default.Chat
                    },
                    contentDescription = null,
                    tint = when {
                        isListening -> Color(0xFFD32F2F)
                        isSpeaking -> FromchemAccentGreen
                        else -> FromchemPrimary
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        isListening -> "Listening... Speak your question"
                        isLoading -> "Gemini is analyzing chemical formulas..."
                        isSpeaking -> "Speaking Fromchem Technical Advice..."
                        else -> "Tap the microphone or choose a voice prompt"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isListening -> Color(0xFFD32F2F)
                        isSpeaking -> FromchemAccentGreen
                        else -> FromchemPrimary
                    }
                )
            }
        }

        // Live Voice Orb & Visualizer
        Box(
            modifier = Modifier
                .size(140.dp)
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated ripple rings
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val baseRadius = size.minDimension / 2.8f

                if (isListening || isSpeaking || isLoading) {
                    val ringColor = if (isListening) Color(0xFFEF5350).copy(alpha = 0.25f)
                    else if (isSpeaking) Color(0xFF4CAF50).copy(alpha = 0.25f)
                    else Color(0xFF0288D1).copy(alpha = 0.25f)

                    drawCircle(
                        color = ringColor,
                        radius = baseRadius * pulseScale,
                        center = center
                    )
                }
            }

            // Big Tactile Microphone Button
            Surface(
                onClick = {
                    if (isSpeaking) {
                        onStopSpeaking()
                    } else {
                        onStartListening()
                    }
                },
                shape = CircleShape,
                color = when {
                    isListening -> Color(0xFFD32F2F)
                    isSpeaking -> FromchemAccentGreen
                    isLoading -> FromchemPrimary.copy(alpha = 0.8f)
                    else -> FromchemPrimary
                },
                shadowElevation = 6.dp,
                modifier = Modifier
                    .size(80.dp)
                    .scale(if (isListening || isSpeaking) pulseScale.coerceAtMost(1.1f) else 1f)
                    .testTag("voice_assistant_mic_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            isSpeaking -> Icons.Default.Stop
                            isListening -> Icons.Default.Mic
                            isLoading -> Icons.Default.Autorenew
                            else -> Icons.Default.Mic
                        },
                        contentDescription = "Speak / Stop",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Spoken Output or Latest Speech Transcript Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                if (lastVoiceTranscript != null) {
                    Text(
                        text = "YOU SPOKE:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextMuted
                    )
                    Text(
                        text = "\"$lastVoiceTranscript\"",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FromchemTextPrimary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                    )
                }

                if (lastAiMessage != null) {
                    Text(
                        text = "FROMCHEM AI RESPONSE:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemPrimary
                    )
                    Text(
                        text = lastAiMessage.text,
                        fontSize = 11.sp,
                        color = FromchemTextSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Voice Prompt Suggestions (Quick Speak)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "OR TAP TO ASK BY VOICE:",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = FromchemTextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(sampleVoiceQueries) { sampleQuery ->
                    SuggestionChip(
                        onClick = { onSelectSampleVoiceQuery(sampleQuery) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = FromchemPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(sampleQuery, fontSize = 10.sp)
                            }
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = FromchemSurfaceVariant,
                            labelColor = FromchemPrimary
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = FromchemBorder
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveVoiceSoundBars(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_bars")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(14.dp)
    ) {
        Box(modifier = Modifier.width(2.dp).height(h1.dp).background(color, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.dp).height(h2.dp).background(color, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.dp).height(h3.dp).background(color, RoundedCornerShape(1.dp)))
    }
}

@Composable
fun LearnMoreDialog(
    cardTitle: String,
    onDismiss: () -> Unit
) {
    val details = when (cardTitle) {
        "Roofing Systems" -> listOf(
            "Elastomeric Liquid Membrane (400% elongation)",
            "Crystalline capillary deep-penetration compound",
            "UV reflective white topcoat (reduces building heat by 6°C)",
            "Seamless application over old tiles or bare concrete",
            "15 Years Certified Manufacturer Warranty"
        )
        "Basement Tanking" -> listOf(
            "Negative & positive side hydrostatic pressure barrier",
            "Resists up to 7 bar water pressure",
            "Non-toxic, safe for drinking water storage tanks",
            "Deep crystalline growth seals hairline cracks up to 0.4mm",
            "Prevents efflorescence and damp wall peeling"
        )
        else -> listOf(
            "Pure Polyurea & Chemical Resistant Epoxy Hybrid",
            "Full resistance to mild acids, alkalis, oils and solvents",
            "High impact and abrasion resistant glossy finish",
            "Quick cure time: Foot traffic in 4 hours, heavy traffic in 24 hours",
            "Zero VOC, eco-friendly solvent-free formulation"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FromchemPrimary),
                shape = RoundedCornerShape(20.dp)
            ) { Text("Got It") }
        },
        title = {
            Text(cardTitle, fontWeight = FontWeight.Bold, color = FromchemTextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Technical Specification & Chemical Features:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemPrimary
                )
                details.forEach { spec ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = FromchemAccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(spec, fontSize = 12.sp, color = FromchemTextSecondary)
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
