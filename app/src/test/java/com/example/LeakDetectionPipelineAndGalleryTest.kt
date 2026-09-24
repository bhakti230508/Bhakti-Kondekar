package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.ai.DetectedDefect
import com.example.ai.GeminiLeakAnalysisResult
import com.example.data.GalleryStorageManager
import com.example.ui.components.GalleryProject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LeakDetectionPipelineAndGalleryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clean up gallery files before each test
        val projectsFile = File(context.filesDir, "saved_gallery_projects.json")
        if (projectsFile.exists()) projectsFile.delete()
        val imagesDir = File(context.filesDir, "gallery_images")
        if (imagesDir.exists()) imagesDir.deleteRecursively()
    }

    @Test
    fun testGalleryStorageManager_persistsAndLoadsProjectsPermanently() {
        // Given: empty initial state
        val initialProjects = GalleryStorageManager.loadProjects(context)
        assertTrue("Initially gallery projects should be empty", initialProjects.isEmpty())

        // When: user persists a project with an image bitmap
        val dummyBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val imageUri = GalleryStorageManager.persistBitmapLocally(context, dummyBitmap)
        assertNotNull("Bitmap must be successfully persisted to local private storage", imageUri)

        val newProject = GalleryProject(
            id = "test_proj_001",
            title = "Factory Roof Waterproofing",
            category = "Roof Waterproofing",
            location = "GIDC Vatva, Ahmedabad",
            areaSize = "15,000 sq ft",
            chemicalUsed = "Fromchem Pure Polyurea 2000",
            durability = "25 Years Warranty",
            description = "Seamless elastomeric roof membrane coating.",
            testResult = "Passed 72hr water ponding test",
            photoCount = 1,
            primaryColor = Color(0xFF0288D1),
            secondaryColor = Color(0xFF00ACC1),
            userUploadedBeforeUri = imageUri
        )

        GalleryStorageManager.saveOrUpdateProject(context, newProject)

        // Then: verify loaded projects from disk
        val loadedProjects = GalleryStorageManager.loadProjects(context)
        assertEquals("Loaded projects count should be 1", 1, loadedProjects.size)
        val loaded = loadedProjects.first()
        assertEquals("test_proj_001", loaded.id)
        assertEquals("Factory Roof Waterproofing", loaded.title)
        assertEquals("Fromchem Pure Polyurea 2000", loaded.chemicalUsed)
        assertEquals(imageUri, loaded.userUploadedBeforeUri)

        // When: project is deleted
        val remaining = GalleryStorageManager.deleteProject(context, "test_proj_001")
        assertTrue("Gallery should be empty after deletion", remaining.isEmpty())
        assertTrue("Disk reload should also be empty", GalleryStorageManager.loadProjects(context).isEmpty())
    }

    @Test
    fun testLeakDetection_irrelevantImageValidation() {
        // State 1: Irrelevant image (Bottle, person, bike, etc.)
        val bottleResult = GeminiLeakAnalysisResult(
            isRelevantForInspection = false,
            detectedObject = "Water Bottle",
            irrelevanceReason = "The image shows a plastic consumer bottle on a desk and does not contain any building or construction surface.",
            detectedIssue = "Not a construction inspection image",
            recommendedApplication = "",
            suggestedNextStep = "Please capture a clear photo of the wall, ceiling, roof, terrace, floor, bathroom, pipe area, or other suspected defective area.",
            severityLevel = "NONE",
            chemicalSpec = "None",
            summary = "Image not suitable for leak detection.",
            defects = emptyList()
        )

        assertFalse("Bottle must be marked as not relevant", bottleResult.isRelevantForInspection)
        assertEquals("Water Bottle", bottleResult.detectedObject)
        assertNotNull("Must contain reason explaining irrelevance", bottleResult.irrelevanceReason)
        assertTrue("No defects should be diagnosed for irrelevant object", bottleResult.defects.isEmpty())
    }

    @Test
    fun testLeakDetection_insufficientQualityValidation() {
        // State 2: Insufficient Quality (Blurry, too dark, out of focus)
        val blurryResult = GeminiLeakAnalysisResult(
            isRelevantForInspection = true,
            isImageQualityInsufficient = true,
            imageQualityMessage = "Image is severely blurred and motion artifacts prevent accurate surface defect classification.",
            detectedIssue = "IMAGE QUALITY INSUFFICIENT",
            recommendedApplication = "",
            suggestedNextStep = "Please capture a clearer and closer image with proper lighting.",
            severityLevel = "LOW",
            chemicalSpec = "N/A",
            summary = "Quality insufficient",
            defects = emptyList()
        )

        assertTrue("Substrate might be relevant", blurryResult.isRelevantForInspection)
        assertTrue("Quality must be flagged as insufficient", blurryResult.isImageQualityInsufficient)
        assertTrue("Must provide reason for insufficiency", blurryResult.imageQualityMessage.contains("blurred"))
    }

    @Test
    fun testLeakDetection_cleanSurfaceValidation() {
        // State 3: Clean surface (No obvious visible defect)
        val cleanWallResult = GeminiLeakAnalysisResult(
            isRelevantForInspection = true,
            isImageQualityInsufficient = false,
            hasVisibleDefects = false,
            noDefectMessage = "No visible moisture stains, efflorescence, blistering, or structural cracking observed.",
            detectedSurface = "Interior Plaster Wall",
            detectedIssue = "No obvious visible defect detected.",
            recommendedApplication = "Regular Maintenance Inspection",
            suggestedNextStep = "Substrate appears structurally sound.",
            severityLevel = "NONE",
            chemicalSpec = "None",
            summary = "Clean wall surface",
            defects = emptyList()
        )

        assertTrue("Wall is relevant", cleanWallResult.isRelevantForInspection)
        assertFalse("Wall is clean without visible defects", cleanWallResult.hasVisibleDefects)
        assertTrue("Defects list must be empty", cleanWallResult.defects.isEmpty())
    }

    @Test
    fun testLeakDetection_defectDetectionWithFromchemMatching() {
        // State 4: Relevant defect detected (Crack on concrete wall)
        val defect = DetectedDefect(
            problemTitle = "Structural Shear Crack",
            shortLabel = "WALL CRACK",
            location = "Mid-height near window frame",
            severity = "MODERATE",
            confidenceScore = 92,
            visualEvidence = "Visible linear fissure approximately 2.5mm wide with slight paint spalling along edges.",
            likelyCause = "Differential foundation settlement or thermal expansion stresses.",
            recommendedAction = "V-groove chase opening, clean dust, inject polyurethane resin grout.",
            fromchemSolution = "Fromchem PU Crack Injection Resin 500",
            fromchemProductSpec = "Two-component low-viscosity hydrophobic polyurethane injection resin"
        )

        val defectResult = GeminiLeakAnalysisResult(
            isRelevantForInspection = true,
            isImageQualityInsufficient = false,
            hasVisibleDefects = true,
            detectedSurface = "Exterior Concrete Wall",
            detectedIssue = "Structural Shear Crack",
            recommendedApplication = "Fromchem PU Crack Injection Resin 500",
            suggestedNextStep = "Chase and inject crack",
            severityLevel = "MODERATE",
            chemicalSpec = "PU Injection Resin",
            summary = "Shear crack detected",
            defects = listOf(defect)
        )

        assertTrue(defectResult.isRelevantForInspection)
        assertTrue(defectResult.hasVisibleDefects)
        assertEquals(1, defectResult.defects.size)

        val analyzedDefect = defectResult.defects.first()
        assertEquals("WALL CRACK", analyzedDefect.shortLabel)
        assertEquals("MODERATE", analyzedDefect.severity)
        assertEquals(92, analyzedDefect.confidenceScore)
        assertEquals("Fromchem PU Crack Injection Resin 500", analyzedDefect.fromchemSolution)
        assertTrue(analyzedDefect.visualEvidence.isNotEmpty())
        assertTrue(analyzedDefect.likelyCause.isNotEmpty())
        assertTrue(analyzedDefect.recommendedAction.isNotEmpty())
    }
}
