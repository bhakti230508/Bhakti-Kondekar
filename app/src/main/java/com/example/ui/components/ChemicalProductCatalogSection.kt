package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ChemicalProduct
import com.example.data.ChemicalProductCatalogData
import com.example.ui.theme.*

@Composable
fun ChemicalProductCatalogSection(
    onRequestQuoteForProduct: (ChemicalProduct) -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Products") }
    var selectedSortBy by remember { mutableStateOf("Featured") }
    var selectedProductForTds by remember { mutableStateOf<ChemicalProduct?>(null) }

    // Filter & Sort Logic
    val filteredProducts = remember(searchQuery, selectedCategory, selectedSortBy) {
        val filtered = ChemicalProductCatalogData.products.filter { product ->
            val matchesCategory = selectedCategory == "All Products" || product.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.chemicalBase.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true) ||
                    product.tagline.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true) ||
                    product.astmStandards.any { it.contains(searchQuery, ignoreCase = true) } ||
                    product.keyFeatures.any { it.contains(searchQuery, ignoreCase = true) } ||
                    product.recommendedSubstrates.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }

        when (selectedSortBy) {
            "Warranty (High to Low)" -> filtered.sortedByDescending { it.warranty }
            "Name (A-Z)" -> filtered.sortedBy { it.name }
            else -> filtered // "Featured" preserves canonical technical order
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("chemical_product_catalog_section")
    ) {
        // Section Header Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = FromchemPrimaryContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = FromchemPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "CHEMICAL SPECIFICATION CATALOG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = FromchemSurfaceVariant
            ) {
                Text(
                    text = "${ChemicalProductCatalogData.products.size} Formulations",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FromchemTextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title & Description
        Text(
            text = "Chemical Waterproofing Products & TDS",
            fontSize = if (isWideScreen) 28.sp else 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = FromchemTextPrimary,
            lineHeight = if (isWideScreen) 34.sp else 28.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Explore laboratory-tested chemical specifications, elongation tolerances, hydrostatic resistance ratings, and ASTM technical data sheets.",
            fontSize = 13.sp,
            color = FromchemTextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Material 3 Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("catalog_search_input"),
            placeholder = {
                Text(
                    text = "Search product name, chemical base (e.g., Polyurea, SBR, Crystalline)...",
                    fontSize = 13.sp,
                    color = FromchemTextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Catalog",
                    tint = if (searchQuery.isNotEmpty()) FromchemPrimary else FromchemTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.testTag("catalog_search_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = FromchemTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = FromchemPrimary,
                unfocusedBorderColor = FromchemBorder,
                focusedTextColor = FromchemTextPrimary,
                unfocusedTextColor = FromchemTextPrimary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Material 3 Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ChemicalProductCatalogData.categories) { category ->
                val isSelected = selectedCategory == category
                val count = if (category == "All Products") {
                    ChemicalProductCatalogData.products.size
                } else {
                    ChemicalProductCatalogData.products.count { it.category.equals(category, ignoreCase = true) }
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = "$category ($count)",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FromchemPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White,
                        containerColor = Color.White,
                        labelColor = FromchemTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = FromchemBorder,
                        selectedBorderColor = FromchemPrimary
                    ),
                    modifier = Modifier.testTag("catalog_filter_${category.replace(" ", "_").lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sort & Active Counter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing ${filteredProducts.size} of ${ChemicalProductCatalogData.products.size} formulations",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FromchemTextSecondary
            )

            // Sort Selector Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted
                )

                val sortOptions = listOf("Featured", "Warranty (High to Low)", "Name (A-Z)")
                sortOptions.forEach { option ->
                    val isCurrentSort = selectedSortBy == option
                    Surface(
                        onClick = { selectedSortBy = option },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrentSort) FromchemPrimaryContainer else Color.Transparent,
                        border = if (isCurrentSort) androidx.compose.foundation.BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.4f)) else null,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = if (option.contains("(")) option.substringBefore("(") else option,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrentSort) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrentSort) FromchemPrimary else FromchemTextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Cards Grid / List
        if (filteredProducts.isEmpty()) {
            // Empty Search State
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .testTag("catalog_empty_state"),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = FromchemSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = FromchemTextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No Chemical Products Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FromchemTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "No waterproofing formulation matched \"$searchQuery\" in \"$selectedCategory\".",
                        fontSize = 12.sp,
                        color = FromchemTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            searchQuery = ""
                            selectedCategory = "All Products"
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Search & Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Display Products in Adaptive Layout
            if (isWideScreen) {
                // Wide Desktop: 2-column grid layout
                val pairs = filteredProducts.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    pairs.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pair.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ChemicalProductCard(
                                        product = product,
                                        onViewTdsClicked = { selectedProductForTds = product },
                                        onRequestQuoteClicked = { onRequestQuoteForProduct(product) }
                                    )
                                }
                            }
                            // Fill remaining space if odd count
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // Mobile: Vertical 1-column stack
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    filteredProducts.forEach { product ->
                        ChemicalProductCard(
                            product = product,
                            onViewTdsClicked = { selectedProductForTds = product },
                            onRequestQuoteClicked = { onRequestQuoteForProduct(product) }
                        )
                    }
                }
            }
        }
    }

    // Full Specification TDS Dialog
    selectedProductForTds?.let { product ->
        ProductTdsSpecificationDialog(
            product = product,
            onDismiss = { selectedProductForTds = null },
            onRequestQuote = {
                selectedProductForTds = null
                onRequestQuoteForProduct(product)
            }
        )
    }
}

@Composable
fun ChemicalProductCard(
    product: ChemicalProduct,
    onViewTdsClicked: () -> Unit,
    onRequestQuoteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Card Top Row: Category + Warranty Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = product.accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = product.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = product.accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FromchemSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = FromchemAccentGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = product.badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FromchemTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product Title
            Text(
                text = product.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = FromchemTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Tagline
            Text(
                text = product.tagline,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = FromchemPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chemical Base Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = FromchemSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = FromchemTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = product.chemicalBase,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = FromchemTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = product.description,
                fontSize = 12.sp,
                color = FromchemTextSecondary,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Key Specs Matrix (4 Chips / Metrics)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FromchemBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecMetricItem(
                            label = "Tensile / Bond",
                            value = product.tensileStrength,
                            modifier = Modifier.weight(1f)
                        )
                        SpecMetricItem(
                            label = "Elongation",
                            value = product.elongation,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = FromchemBorder.copy(alpha = 0.6f), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecMetricItem(
                            label = "Curing Speed",
                            value = product.curingTime.take(28),
                            modifier = Modifier.weight(1f)
                        )
                        SpecMetricItem(
                            label = "Hydrostatic Rating",
                            value = product.hydrostaticResistance,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ASTM / Certified Standards Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Standards:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted
                )
                product.astmStandards.take(3).forEach { std ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = FromchemSurfaceVariant
                    ) {
                        Text(
                            text = std,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FromchemTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Full TDS Button
                OutlinedButton(
                    onClick = onViewTdsClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("view_tds_button_${product.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Full TDS Specs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Request Quote / Sample Button
                Button(
                    onClick = onRequestQuoteClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("quote_button_${product.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FromchemPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Get Quote",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecMetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = FromchemTextMuted,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = FromchemTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProductTdsSpecificationDialog(
    product: ChemicalProduct,
    onDismiss: () -> Unit,
    onRequestQuote: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 680.dp)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .testTag("product_tds_dialog_${product.id}"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = product.accentColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = product.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Technical Data Sheet (TDS)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FromchemAccentGreen
                                ) {
                                    Text(
                                        text = "LAB CERTIFIED",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Standard Reference: ISO 9001:2015 & ASTM",
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
                            contentDescription = "Close TDS",
                            tint = FromchemTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Title Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FromchemPrimaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                            Text(
                                text = "Grade: ${product.chemicalGrade}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = FromchemPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FromchemTextPrimary
                        )
                        Text(
                            text = product.tagline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = FromchemPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full Chemical Specification Table
                Text(
                    text = "PHYSICAL & CHEMICAL PERFORMANCE PARAMETERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        TdsTableRow("Chemical Base", product.chemicalBase, isEven = false)
                        TdsTableRow("Tensile / Adhesion", product.tensileStrength, isEven = true)
                        TdsTableRow("Elongation at Break", product.elongation, isEven = false)
                        TdsTableRow("Curing / Setting Time", product.curingTime, isEven = true)
                        TdsTableRow("Solid Polymer Content", product.solidsContent, isEven = false)
                        TdsTableRow("Nominal Coverage Rate", product.coverage, isEven = true)
                        TdsTableRow("Hydrostatic Resistance", product.hydrostaticResistance, isEven = false)
                        TdsTableRow("Service Temperature", product.serviceTemperature, isEven = true)
                        TdsTableRow("VOC / Emissions", product.vocContent, isEven = false)
                        TdsTableRow("Application Technique", product.applicationMethod, isEven = true)
                        TdsTableRow("Warranty Duration", product.warranty, isEven = false)
                        TdsTableRow("Standard Pack Size", product.packSize, isEven = true)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Laboratory Tested Standards
                Text(
                    text = "APPLICABLE COMPLIANCE STANDARDS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.astmStandards.forEach { std ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FromchemSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = FromchemAccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = std,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FromchemTextPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Key Chemical Advantages
                Text(
                    text = "FORMULATION ADVANTAGES & PERFORMANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FromchemBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FromchemBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        product.keyFeatures.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = FromchemPrimary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Text(
                                    text = feature,
                                    fontSize = 12.sp,
                                    color = FromchemTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recommended Substrates
                Text(
                    text = "RECOMMENDED SUBSTRATES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextMuted,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(product.recommendedSubstrates) { sub ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FromchemSurfaceVariant
                        ) {
                            Text(
                                text = "• $sub",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = FromchemTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dialog Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "TDS for ${product.name} downloaded to device cache.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FromchemPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download TDS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onRequestQuote,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .testTag("tds_dialog_order_quote_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FromchemPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.RequestQuote,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Get Product Quote", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TdsTableRow(
    title: String,
    value: String,
    isEven: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEven) FromchemSurfaceVariant.copy(alpha = 0.4f) else Color.Transparent)
            .padding(vertical = 7.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = FromchemTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = FromchemTextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
    }
}
