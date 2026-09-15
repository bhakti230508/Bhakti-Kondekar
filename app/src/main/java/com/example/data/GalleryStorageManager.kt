package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ui.components.GalleryProject
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Manages local, permanent storage of Gallery images and projects on the device.
 * Guarantees that user-uploaded photos are copied into the app's private files directory,
 * ensuring they remain permanently available across app restarts and process kills
 * without relying on transient Photo Picker URI permissions.
 */
object GalleryStorageManager {
    private const val TAG = "GalleryStorageManager"
    private const val DIR_GALLERY_IMAGES = "gallery_images"
    private const val FILE_GALLERY_PROJECTS = "saved_gallery_projects.json"

    /**
     * Copies a source Uri into the app's internal private storage permanently.
     * Returns the permanent file Uri, or null on failure.
     */
    fun persistImageLocally(context: Context, sourceUri: Uri): Uri? {
        return try {
            val imagesDir = File(context.filesDir, DIR_GALLERY_IMAGES).apply {
                if (!exists()) mkdirs()
            }

            // If it's already a file URI inside our private gallery directory, return it
            if (sourceUri.scheme == "file" && sourceUri.path?.startsWith(imagesDir.absolutePath) == true) {
                return sourceUri
            }

            val fileName = "gallery_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val destFile = File(imagesDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return null

            Uri.fromFile(destFile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy image to internal storage: ${e.message}", e)
            null
        }
    }

    /**
     * Persists a Bitmap into the app's internal private storage permanently.
     */
    fun persistBitmapLocally(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val imagesDir = File(context.filesDir, DIR_GALLERY_IMAGES).apply {
                if (!exists()) mkdirs()
            }
            val fileName = "gallery_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val destFile = File(imagesDir, fileName)

            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            Uri.fromFile(destFile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to persist bitmap to internal storage: ${e.message}", e)
            null
        }
    }

    /**
     * Loads all permanently saved gallery projects from disk.
     * If no projects have been saved yet, returns an empty list so that no default/sample
     * images are displayed.
     */
    fun loadProjects(context: Context): List<GalleryProject> {
        val file = File(context.filesDir, FILE_GALLERY_PROJECTS)
        if (!file.exists()) {
            return emptyList()
        }

        return try {
            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)
            val projects = mutableListOf<GalleryProject>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val beforeUriStr = obj.optString("userUploadedBeforeUri", null)
                val afterUriStr = obj.optString("userUploadedAfterUri", null)
                val compUriStr = obj.optString("userUploadedComparisonUri", null)

                val multiUrisArray = obj.optJSONArray("userUploadedOriginalUris")
                val multiUris = mutableListOf<Uri>()
                if (multiUrisArray != null) {
                    for (j in 0 until multiUrisArray.length()) {
                        val uriStr = multiUrisArray.optString(j)
                        if (!uriStr.isNullOrBlank()) {
                            multiUris.add(Uri.parse(uriStr))
                        }
                    }
                }

                val primaryColorInt = obj.optInt("primaryColor", Color(0xFF0288D1).toArgb())
                val secondaryColorInt = obj.optInt("secondaryColor", Color(0xFF00ACC1).toArgb())

                val project = GalleryProject(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    title = obj.optString("title", "Waterproofing Project"),
                    category = obj.optString("category", "Roof Waterproofing"),
                    location = obj.optString("location", "Site Inspection"),
                    areaSize = obj.optString("areaSize", "Site Area"),
                    chemicalUsed = obj.optString("chemicalUsed", "Fromchem Chemical System"),
                    durability = obj.optString("durability", "15 Years Warranty"),
                    description = obj.optString("description", "Original site waterproofing photo."),
                    testResult = obj.optString("testResult", "Hydrostatic Water Resistance Certified"),
                    photoCount = obj.optInt("photoCount", 1),
                    primaryColor = Color(primaryColorInt),
                    secondaryColor = Color(secondaryColorInt),
                    beforeRes = null, // Strictly no default/sample drawable images
                    afterRes = null,
                    comparisonRes = null,
                    userUploadedOriginalUris = multiUris,
                    userUploadedBeforeUri = if (!beforeUriStr.isNullOrBlank()) Uri.parse(beforeUriStr) else null,
                    userUploadedAfterUri = if (!afterUriStr.isNullOrBlank()) Uri.parse(afterUriStr) else null,
                    userUploadedComparisonUri = if (!compUriStr.isNullOrBlank()) Uri.parse(compUriStr) else null,
                    originalImageUrl = obj.optString("originalImageUrl", null)
                )
                projects.add(project)
            }
            projects
        } catch (e: Exception) {
            Log.e(TAG, "Error loading saved gallery projects: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Saves the given list of gallery projects to disk permanently.
     */
    fun saveProjects(context: Context, projects: List<GalleryProject>) {
        try {
            val jsonArray = JSONArray()
            for (p in projects) {
                val obj = JSONObject()
                obj.put("id", p.id)
                obj.put("title", p.title)
                obj.put("category", p.category)
                obj.put("location", p.location)
                obj.put("areaSize", p.areaSize)
                obj.put("chemicalUsed", p.chemicalUsed)
                obj.put("durability", p.durability)
                obj.put("description", p.description)
                obj.put("testResult", p.testResult)
                obj.put("photoCount", p.photoCount)
                obj.put("primaryColor", p.primaryColor.toArgb())
                obj.put("secondaryColor", p.secondaryColor.toArgb())

                p.userUploadedBeforeUri?.let { obj.put("userUploadedBeforeUri", it.toString()) }
                p.userUploadedAfterUri?.let { obj.put("userUploadedAfterUri", it.toString()) }
                p.userUploadedComparisonUri?.let { obj.put("userUploadedComparisonUri", it.toString()) }
                p.originalImageUrl?.let { obj.put("originalImageUrl", it) }

                if (p.userUploadedOriginalUris.isNotEmpty()) {
                    val arr = JSONArray()
                    p.userUploadedOriginalUris.forEach { arr.put(it.toString()) }
                    obj.put("userUploadedOriginalUris", arr)
                }

                jsonArray.put(obj)
            }

            val file = File(context.filesDir, FILE_GALLERY_PROJECTS)
            file.writeText(jsonArray.toString())
            Log.d(TAG, "Successfully saved ${projects.size} gallery projects to disk.")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving gallery projects to disk: ${e.message}", e)
        }
    }

    /**
     * Adds or updates a project and persists immediately to disk.
     * Returns the updated list.
     */
    fun saveOrUpdateProject(context: Context, project: GalleryProject): List<GalleryProject> {
        val currentList = loadProjects(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            currentList[index] = project
        } else {
            currentList.add(0, project) // Prepend so newly added project shows first
        }
        saveProjects(context, currentList)
        return currentList
    }

    /**
     * Deletes a project by ID and persists immediately to disk.
     * Returns the updated list.
     */
    fun deleteProject(context: Context, projectId: String): List<GalleryProject> {
        val currentList = loadProjects(context).toMutableList()
        val toRemove = currentList.firstOrNull { it.id == projectId }
        if (toRemove != null) {
            // Best effort cleanup of local image files
            val imagesDir = File(context.filesDir, DIR_GALLERY_IMAGES)
            fun tryDelete(uri: Uri?) {
                if (uri?.scheme == "file") {
                    val f = uri.path?.let { File(it) }
                    if (f != null && f.startsWith(imagesDir)) {
                        f.delete()
                    }
                }
            }
            tryDelete(toRemove.userUploadedBeforeUri)
            tryDelete(toRemove.userUploadedAfterUri)
            tryDelete(toRemove.userUploadedComparisonUri)
            toRemove.userUploadedOriginalUris.forEach { tryDelete(it) }

            currentList.remove(toRemove)
            saveProjects(context, currentList)
        }
        return currentList
    }
}
