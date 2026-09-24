package com.example

import com.example.data.ChemicalProductCatalogData
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ChemicalProductCatalogTest {

    @Test
    fun testCatalogData_containsExpectedProductsAndSpecifications() {
        val products = ChemicalProductCatalogData.products
        assertTrue("Catalog must have multiple chemical waterproofing formulations", products.size >= 8)

        // Verify each product has complete chemical and technical specifications
        products.forEach { product ->
            assertFalse("Product ID must not be empty", product.id.isBlank())
            assertFalse("Product name must not be empty", product.name.isBlank())
            assertFalse("Category must not be empty", product.category.isBlank())
            assertFalse("Chemical base must be defined", product.chemicalBase.isBlank())
            assertFalse("Tensile strength must be specified", product.tensileStrength.isBlank())
            assertFalse("Elongation must be specified", product.elongation.isBlank())
            assertFalse("Curing time must be specified", product.curingTime.isBlank())
            assertFalse("Hydrostatic resistance must be specified", product.hydrostaticResistance.isBlank())
            assertFalse("Warranty must be specified", product.warranty.isBlank())
            assertTrue("Product must have at least one ASTM/EN standard", product.astmStandards.isNotEmpty())
            assertTrue("Product must have key features", product.keyFeatures.isNotEmpty())
            assertTrue("Product must have recommended substrates", product.recommendedSubstrates.isNotEmpty())
        }
    }

    @Test
    fun testCatalogFiltering_byCategory() {
        val roofProducts = ChemicalProductCatalogData.products.filter {
            it.category.equals("Roof & Terrace", ignoreCase = true)
        }
        assertTrue("Should contain roof products", roofProducts.isNotEmpty())
        assertTrue("Polyurea should be in Roof & Terrace", roofProducts.any { it.id == "polyurea-2000" })

        val basementProducts = ChemicalProductCatalogData.products.filter {
            it.category.equals("Basement Tanking", ignoreCase = true)
        }
        assertTrue("Should contain basement products", basementProducts.isNotEmpty())
        assertTrue("Crystalline should be in Basement Tanking", basementProducts.any { it.id == "crystalline-50" })
    }

    @Test
    fun testCatalogSearch_byChemicalKeyword() {
        val query = "polyurea"
        val matchedProducts = ChemicalProductCatalogData.products.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
            product.chemicalBase.contains(query, ignoreCase = true) ||
            product.description.contains(query, ignoreCase = true)
        }
        assertTrue("Searching for 'polyurea' should find at least one product", matchedProducts.isNotEmpty())
        assertEquals("polyurea-2000", matchedProducts.first().id)

        val crystallineQuery = "crystalline"
        val matchedCrystalline = ChemicalProductCatalogData.products.filter { product ->
            product.name.contains(crystallineQuery, ignoreCase = true) ||
            product.chemicalBase.contains(crystallineQuery, ignoreCase = true)
        }
        assertTrue("Searching for 'crystalline' should find Crystalline 50", matchedCrystalline.isNotEmpty())
    }

    @Test
    fun testCatalogSorting_byNameAndWarranty() {
        val sortedByName = ChemicalProductCatalogData.products.sortedBy { it.name }
        assertTrue("Sorted by name must be alphabetical", sortedByName.first().name <= sortedByName.last().name)

        val sortedByWarranty = ChemicalProductCatalogData.products.sortedByDescending { it.warranty }
        assertNotNull("Sorted list should not be null", sortedByWarranty)
        assertEquals(ChemicalProductCatalogData.products.size, sortedByWarranty.size)
    }
}
