package com.example.smartfarm.domain

data class DiseaseAnalysisResult(
    val label: String?,
    val confidence: Double,
    val confidenceGrade: String, // "High", "Medium", "Low", "Uncertain"
    val isConfident: Boolean,
    val isDemo: Boolean,
    val problemExplanation: String,
    val whatToDoNext: String,
    val recommendedProductIds: List<String>,
    val modelVersion: String = "SmartFarm-Cassava-v1-demo"
)

interface DiseaseModelAdapter {
    val id: String
    val isDemo: Boolean
    val adapterName: String

    suspend fun analyse(imageNameOrUri: String): DiseaseAnalysisResult
}

class DemoDiseaseAdapter : DiseaseModelAdapter {
    override val id: String = "demo"
    override val isDemo: Boolean = true
    override val adapterName: String = "Demo Model Adapter (Test / Sample Mode)"

    companion object {
        const val CONFIDENCE_THRESHOLD = 0.70
    }

    override suspend fun analyse(imageNameOrUri: String): DiseaseAnalysisResult {
        // Deterministic or pseudo-realistic diagnosis based on input test selection
        val lower = imageNameOrUri.lowercase()

        return when {
            lower.contains("bacterial") || lower.contains("cbb") -> {
                DiseaseAnalysisResult(
                    label = "Cassava Bacterial Blight",
                    confidence = 0.88,
                    confidenceGrade = "High",
                    isConfident = true,
                    isDemo = true,
                    problemExplanation = "Cassava Bacterial Blight causes angular water-soaked spots on leaves and wilting of shoots. It can spread quickly during rainy conditions.",
                    whatToDoNext = "1. Cut and destroy infected stems away from field.\n2. Do not use infected cuttings for next planting.\n3. Disinfect harvesting tools with bleach solution.\n4. Speak with our agricultural consultant for guidance.",
                    recommendedProductIds = listOf("PROD-001", "PROD-004") // Certified clean stems, copper bactericide
                )
            }
            lower.contains("brown") || lower.contains("cbsd") -> {
                DiseaseAnalysisResult(
                    label = "Cassava Brown Streak Disease",
                    confidence = 0.82,
                    confidenceGrade = "High",
                    isConfident = true,
                    isDemo = true,
                    problemExplanation = "Cassava Brown Streak Disease causes yellowing along leaf veins and dry brown rot inside the tuberous roots.",
                    whatToDoNext = "1. Harvest roots early to prevent rot progression.\n2. Plant only certified disease-free stem cuttings.\n3. Manage whitefly populations in the field.\n4. Request our expert treatment plan.",
                    recommendedProductIds = listOf("PROD-001", "PROD-005")
                )
            }
            lower.contains("mite") || lower.contains("cgm") -> {
                DiseaseAnalysisResult(
                    label = "Cassava Green Mite",
                    confidence = 0.78,
                    confidenceGrade = "Medium",
                    isConfident = true,
                    isDemo = true,
                    problemExplanation = "Cassava Green Mite causes yellow speckling on upper leaf surfaces and can cause candlestick appearance of top stems during dry periods.",
                    whatToDoNext = "1. Conserve predatory mites and natural beneficial insects.\n2. Avoid unapproved chemical sprays that kill predators.\n3. Plant tolerant varieties.\n4. Ask our expert for biological management options.",
                    recommendedProductIds = listOf("PROD-003", "PROD-005")
                )
            }
            lower.contains("healthy") -> {
                DiseaseAnalysisResult(
                    label = "Healthy Cassava Leaf",
                    confidence = 0.94,
                    confidenceGrade = "High",
                    isConfident = true,
                    isDemo = true,
                    problemExplanation = "No symptoms of major cassava diseases detected. Leaves appear uniform, green, and well-developed.",
                    whatToDoNext = "1. Continue regular field monitoring every 2 weeks.\n2. Maintain good weed management.\n3. Ensure adequate soil nutrition for tuber bulking.",
                    recommendedProductIds = listOf("PROD-002") // NPK Fertilizer
                )
            }
            lower.contains("blurry") || lower.contains("uncertain") || lower.contains("low") -> {
                DiseaseAnalysisResult(
                    label = null,
                    confidence = 0.45,
                    confidenceGrade = "Uncertain",
                    isConfident = false,
                    isDemo = true,
                    problemExplanation = "We are not sure from this picture. The image may be too blurry, too dark, or taken from too far away.",
                    whatToDoNext = "Take another clear picture holding one leaf flat in good daylight, or talk directly with our agricultural expert.",
                    recommendedProductIds = emptyList()
                )
            }
            else -> {
                // Default: Cassava Mosaic Disease
                DiseaseAnalysisResult(
                    label = "Cassava Mosaic Disease",
                    confidence = 0.89,
                    confidenceGrade = "High",
                    isConfident = true,
                    isDemo = true,
                    problemExplanation = "Cassava Mosaic Disease causes distorted, patchy green and yellow mosaic leaves and stunts root growth. It is spread by whiteflies and infected stem cuttings.",
                    whatToDoNext = "1. Rogue (uproot) severely affected young plants.\n2. Select disease-resistant stems (e.g., TME 419) for next planting.\n3. Control whitefly insect vectors.\n4. Request our expert treatment plan for localized advice.",
                    recommendedProductIds = listOf("PROD-001", "PROD-003", "PROD-004")
                )
            }
        }
    }
}
