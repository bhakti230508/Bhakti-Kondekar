package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.ai.GeminiLeakAnalysisResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Result state container for the complete Firebase Storage + Gemini + Firestore workflow.
 */
sealed class FirebaseUploadResult {
    data class Success(
        val downloadUrl: String,
        val storagePath: String,
        val firestoreDocId: String,
        val geminiResult: GeminiLeakAnalysisResult
    ) : FirebaseUploadResult()

    data class StorageSuccessFirestoreFailed(
        val downloadUrl: String,
        val storagePath: String,
        val geminiResult: GeminiLeakAnalysisResult,
        val firestoreError: String
    ) : FirebaseUploadResult()

    data class StorageFailedGeminiSucceeded(
        val geminiResult: GeminiLeakAnalysisResult,
        val storageError: String
    ) : FirebaseUploadResult()

    data class CompleteFailure(
        val errorMessage: String,
        val geminiResult: GeminiLeakAnalysisResult? = null
    ) : FirebaseUploadResult()
}

object FirebasePhotoManager {
    private const val TAG = "FirebasePhotoManager"
    private const val COLLECTION_PHOTOS = "photos"

    /**
     * Checks whether Firebase is properly initialized in the app.
     */
    fun isFirebaseInitialized(context: Context): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp check failed: ${e.message}")
            false
        }
    }

    /**
     * Retrieves the current authenticated user ID, or null if unauthenticated.
     */
    fun getCurrentUserId(): String? {
        return try {
            FirebaseAuth.getInstance().currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Converts a Bitmap to compressed JPEG ByteArray.
     */
    fun bitmapToByteArray(bitmap: Bitmap, quality: Int = 85): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }

    /**
     * Converts an image Uri to ByteArray using Context ContentResolver.
     */
    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read bytes from Uri: $uri", e)
            null
        }
    }

    /**
     * Uploads image bytes to Firebase Cloud Storage.
     * Path pattern: images/{userId-or-device-id}/{timestamp}_{sanitizedFileName}
     *
     * @return Result containing Pair(downloadUrl, storagePath)
     */
    suspend fun uploadImageToStorage(
        context: Context,
        imageBytes: ByteArray,
        rawFileName: String? = null,
        explicitUserId: String? = null
    ): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        if (!isFirebaseInitialized(context)) {
            val errorMsg = "Firebase is not initialized. Please ensure google-services.json is in the app/ directory."
            Log.e(TAG, errorMsg)
            return@withContext Result.failure(IllegalStateException(errorMsg))
        }

        try {
            val storage = FirebaseStorage.getInstance()
            val userId = explicitUserId ?: getCurrentUserId() ?: "guest_user"
            val timestamp = System.currentTimeMillis()
            val timeFormatted = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(timestamp))
            
            val sanitizedName = if (!rawFileName.isNullOrBlank()) {
                rawFileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
            } else {
                "scan_${timeFormatted}_${UUID.randomUUID().toString().take(6)}.jpg"
            }

            val storagePath = "images/$userId/${timestamp}_$sanitizedName"
            val storageRef = storage.reference.child(storagePath)

            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setCustomMetadata("uploadedBy", userId)
                .setCustomMetadata("appName", "Fromchem Solution")
                .build()

            // 1. Upload bytes to Firebase Storage
            val uploadTask = storageRef.putBytes(imageBytes, metadata).await()
            Log.d(TAG, "Image uploaded successfully to Storage: ${uploadTask.metadata?.path}")

            // 2. Obtain download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d(TAG, "Download URL obtained: $downloadUrl")

            Result.success(Pair(downloadUrl, storagePath))
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Storage upload error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves a metadata and analysis record to Cloud Firestore in the "photos" collection.
     *
     * Document structure:
     * photos/{autoGeneratedDocumentId}
     * {
     *   "imageUrl": "...",
     *   "fileName": "...",
     *   "storagePath": "...",
     *   "uploadedAt": FieldValue.serverTimestamp(),
     *   "geminiResult": { ... },
     *   "status": "success",
     *   "userId": "..." // if present
     * }
     */
    suspend fun savePhotoRecordToFirestore(
        context: Context,
        imageUrl: String,
        fileName: String,
        storagePath: String,
        geminiResult: GeminiLeakAnalysisResult?,
        explicitUserId: String? = null,
        status: String = "success",
        errorMessage: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isFirebaseInitialized(context)) {
            val errorMsg = "Firebase is not initialized. Cannot save to Firestore."
            Log.e(TAG, errorMsg)
            return@withContext Result.failure(IllegalStateException(errorMsg))
        }

        try {
            val firestore = FirebaseFirestore.getInstance()
            val userId = explicitUserId ?: getCurrentUserId()

            val docData = mutableMapOf<String, Any?>(
                "imageUrl" to imageUrl,
                "fileName" to fileName,
                "storagePath" to storagePath,
                "uploadedAt" to FieldValue.serverTimestamp(),
                "status" to status
            )

            if (userId != null && userId.isNotBlank()) {
                docData["userId"] = userId
            }

            if (geminiResult != null) {
                docData["geminiResult"] = geminiResult.toMap()
            }

            if (!errorMessage.isNullOrBlank()) {
                docData["errorMessage"] = errorMessage
            }

            val docRef = firestore.collection(COLLECTION_PHOTOS).document()
            docRef.set(docData).await()

            Log.d(TAG, "Firestore document written with ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore write error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
