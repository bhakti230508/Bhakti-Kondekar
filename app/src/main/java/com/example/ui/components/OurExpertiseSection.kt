package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ExpertiseCardData(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val graphicType: String
)

@Composable
fun OurExpertiseSection(
    onCardClicked: (ExpertiseCardData) -> Unit,
    onChatClicked: () -> Unit,
    isWideScreen: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Commercial") }

    val cards = listOf(
        ExpertiseCardData(
            id = "roofing",
            title = "Roofing Systems",
            description = if (selectedCategory == "Commercial")
                "Liquid-applied membranes and crystalline coatings designed to withstand extreme thermal expansion and UV exposure."
            else
                "Elastomeric rooftop seals for residential villas and apartment terraces to prevent monsoon rain seepage.",
            icon = Icons.Default.Roofing,
            graphicType = "roofing"
        ),
        ExpertiseCardData(
            id = "basement",
            title = "Basement Tanking",
            description = if (selectedCategory == "Commercial")
                "Heavy-duty chemical tanking solutions that prevent hydrostatic pressure from compromising below-ground structures."
            else
                "Waterproofing barriers for residential cellars, parking pits, and sump tanks against groundwater seepage.",
            icon = Icons.Default.Foundation,
            graphicType = "basement"
        ),
        ExpertiseCardData(
            id = "industrial",
            title = "Industrial Coating",
            description = if (selectedCategory == "Commercial")
                "Chemical-resistant barriers for warehouses and processing plants, ensuring long-term structural integrity against spills."
            else
                "High-performance chemical resistant coatings for driveways, garage floors, and utility utility rooms.",
            icon = Icons.Default.Factory,
            graphicType = "industrial"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Top Header Row with Title and Chat Bubble / Commercial-Residential Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Our Expertise",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Specialized solutions tailored for every layer of your building's architecture.",
                    fontSize = 13.sp,
                    color = FromchemTextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Floating Chat Bubble with Red Indicator Dot
            Box(
                modifier = Modifier
                    .testTag("chat_bubble_button")
                    .size(44.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onChatClicked() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Live Chat Inquiry",
                    tint = FromchemPrimary,
                    modifier = Modifier.size(20.dp)
                )
                // Red Notification Dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FromchemAccentRed)
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Commercial / Residential Toggle Bar (Aligned Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(3.dp)
            ) {
                listOf("Commercial", "Residential").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) FromchemPrimary else FromchemTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Expertise Cards Grid or Stack
        if (isWideScreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                cards.forEach { card ->
                    Box(modifier = Modifier.weight(1f)) {
                        ExpertiseCardItem(card = card, onClick = { onCardClicked(card) })
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                cards.forEach { card ->
                    ExpertiseCardItem(card = card, onClick = { onCardClicked(card) })
                }
            }
        }
    }
}

@Composable
fun ExpertiseCardItem(
    card: ExpertiseCardData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expertise_card_${card.id}")
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Rounded Icon Box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FromchemPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = card.title,
                    tint = FromchemPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = card.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FromchemTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.description,
                fontSize = 12.sp,
                color = FromchemTextSecondary,
                lineHeight = 18.sp,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Graphic Illustration Component
            when (card.graphicType) {
                "roofing" -> RoofingSystemGraphic()
                "basement" -> BasementTankingGraphic()
                "industrial" -> IndustrialCoatingGraphic()
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Learn More Link Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { onClick() }
            ) {
                Text(
                    text = "Learn more",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FromchemPrimary
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Learn More",
                    tint = FromchemPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
