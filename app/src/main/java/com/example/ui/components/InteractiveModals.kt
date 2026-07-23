package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ChatMessageItem
import com.example.ai.GeminiChatService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

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
                    val estCostMin = (estArea * 3.5).toInt()
                    val estCostMax = (estArea * 5.2).toInt()

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
                                text = "\$$estCostMin - \$$estCostMax (ISO Certified Crystalline Seal)",
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
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessageItem(
                    sender = "ai",
                    text = "Hello! I am your Fromchem AI Chemical Engineer powered by Gemini. Ask me anything about crystalline technology, elastomeric roof sealing, basement tanking, or chemical compatibility!"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val suggestedQuestions = listOf(
        "What is Crystalline Waterproofing?",
        "How fast does Polyurea cure?",
        "Best waterproofing for basements?",
        "How does the 15-year warranty work?"
    )

    fun sendQuery(userText: String) {
        if (userText.isBlank() || isLoading) return
        val userMsg = ChatMessageItem(sender = "user", text = userText)
        val updatedHistory = messages + userMsg
        messages = updatedHistory
        inputText = ""
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
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("chat_close_button")
            ) {
                Text("Close", color = FromchemTextSecondary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            if (messages.size > 1) {
                IconButton(
                    onClick = {
                        messages = listOf(
                            ChatMessageItem(
                                sender = "ai",
                                text = "Chat history cleared. How can I assist you with your waterproofing project?"
                            )
                        )
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
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FromchemPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = FromchemPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Fromchem AI Engineer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = FromchemTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(FromchemAccentGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Powered by Gemini 3.5 Flash",
                                fontSize = 10.sp,
                                color = FromchemTextSecondary
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
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
                                    Text(
                                        text = msg.text,
                                        fontSize = 12.sp,
                                        color = if (isUser) Color.White else FromchemTextPrimary,
                                        modifier = Modifier.padding(12.dp),
                                        lineHeight = 17.sp
                                    )
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

                // Input Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask Gemini about chemical seals...", fontSize = 12.sp) },
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
                    IconButton(
                        onClick = { sendQuery(inputText) },
                        enabled = inputText.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .testTag("chat_send_button")
                            .size(42.dp)
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
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
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
