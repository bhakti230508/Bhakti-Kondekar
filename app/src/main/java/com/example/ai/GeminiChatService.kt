package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessageItem(
    val sender: String, // "user" or "ai" or "system"
    val text: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

object GeminiChatService {
    private const val TAG = "GeminiChatService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_INSTRUCTION = """
        You are Fromchem AI, the official AI Chemical Waterproofing Engineer for Fromchem Solution.
        Your expertise includes:
        - Crystalline waterproofing technology & capillary pore sealing
        - Liquid-applied elastomeric membranes for roofs and terraces
        - Heavy-duty chemical basement tanking for high hydrostatic pressure
        - High-gloss polyurea & epoxy coatings for industrial factory floors
        - 15-year monolithic substrate warranties & ISO certified chemical compounds

        Be helpful, professional, concise, and friendly. Provide practical technical advice on structural protection, curing times, surface preparation, and chemical compatibility.
    """

    suspend fun sendMessage(
        history: List<ChatMessageItem>,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            var apiKey = ""
            try {
                apiKey = BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                Log.w(TAG, "GEMINI_API_KEY build config field not found: ${e.message}")
            }

            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "null") {
                // Return a friendly fallback if API key is not configured in secrets
                val localFallback = generateLocalFallbackResponse(userMessage)
                return@withContext Result.success(localFallback)
            }

            // Build request JSON
            val requestJson = JSONObject()

            // System Instruction
            val systemInstructionObj = JSONObject()
            val sysPartsArray = JSONArray()
            sysPartsArray.put(JSONObject().put("text", SYSTEM_INSTRUCTION))
            systemInstructionObj.put("parts", sysPartsArray)
            requestJson.put("systemInstruction", systemInstructionObj)

            // Conversation history
            val contentsArray = JSONArray()

            // Pass previous turns
            history.takeLast(10).forEach { item ->
                if (item.sender == "user" || item.sender == "ai") {
                    val role = if (item.sender == "user") "user" else "model"
                    val contentObj = JSONObject()
                    contentObj.put("role", role)
                    val partsArr = JSONArray()
                    partsArr.put(JSONObject().put("text", item.text))
                    contentObj.put("parts", partsArr)
                    contentsArray.put(contentObj)
                }
            }

            // Current user turn
            val currentUserContent = JSONObject()
            currentUserContent.put("role", "user")
            val currentPartsArr = JSONArray()
            currentPartsArr.put(JSONObject().put("text", userMessage))
            currentUserContent.put("parts", currentPartsArr)
            contentsArray.put(currentUserContent)

            requestJson.put("contents", contentsArray)

            // Generation config
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7)
            generationConfig.put("maxOutputTokens", 800)
            requestJson.put("generationConfig", generationConfig)

            val requestUrl = "$BASE_URL?key=$apiKey"
            val body = requestJson.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(requestUrl)
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API call failed code ${response.code}: $responseBodyString")
                // If API key is invalid or quota exceeded, handle gracefully
                val fallback = generateLocalFallbackResponse(userMessage)
                return@withContext Result.success(fallback)
            }

            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext Result.success(text.trim())
                    }
                }
            }

            Result.success(generateLocalFallbackResponse(userMessage))
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API", e)
            Result.success(generateLocalFallbackResponse(userMessage))
        }
    }

    private fun generateLocalFallbackResponse(userQuery: String): String {
        val q = userQuery.lowercase()
        return when {
            q.contains("crystalline") ->
                "Crystalline waterproofing works through active catalytic chemical reactions. When applied to concrete, crystalline molecules penetrate micro-capillaries and react with moisture and unhydrated cement particles to form insoluble needle-shaped crystals. This permanently seals the concrete against hydrostatic pressure!"
            q.contains("roof") || q.contains("terrace") ->
                "For roofs and terraces, Fromchem recommends our Liquid-Applied Elastomeric Membrane. It provides seamless 400% elongation, accommodating thermal expansion without cracking, while reflecting up to 85% of solar radiation to reduce rooftop heat."
            q.contains("basement") || q.contains("tank") ->
                "Basement tanking requires heavy-duty chemical barrier slurry that withstands positive and negative hydrostatic groundwater pressure (up to 7 bar). It prevents dampness, salt efflorescence, and water ingress in cellars and parking structures."
            q.contains("polyurea") || q.contains("cure") || q.contains("curing") ->
                "Fromchem Polyurea coatings feature ultra-fast curing times! They are tack-free in under 60 seconds and allow light foot traffic within 4 to 6 hours. Perfect for industrial floors that require rapid re-commissioning."
            q.contains("warranty") || q.contains("guarantee") ->
                "All Fromchem certified chemical waterproofing installations come backed by our 15-Year Monolithic Substrate Guarantee, covering both chemical material integrity and structural seal performance."
            q.contains("cost") || q.contains("quote") || q.contains("price") ->
                "You can get an instant material and application estimate right inside this app! Tap the 'Get Quote' button in the top bar to calculate prices based on your project area in square feet."
            else ->
                "Fromchem Solution specializes in advanced chemical waterproofing technology. Whether you need basement tanking, elastomeric roof membranes, or chemical-resistant polyurea flooring, our ISO-certified formulations provide monolithic, zero-seam structural protection."
        }
    }
}
