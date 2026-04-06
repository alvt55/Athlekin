package com.example.athlekin.datasource

import com.example.athlekin.model.PlateauAnalysis
import com.google.android.gms.common.util.JsonUtils
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import javax.inject.Inject


class GeminiDataSource @Inject constructor() {


    @OptIn(PublicPreviewAPI::class)
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .templateGenerativeModel()


    // returns string response from a prompt
    @OptIn(PublicPreviewAPI::class)
    suspend fun generateTextFromPrompt(analysisObj: PlateauAnalysis): String? {
        return try {
            val response = model.generateContent(
                "plateau-template-v1",
                mapOf(
                    // TEST analysis object, observing the parameters for template prompting
                    "analysis" to mapOf(
                        "exerciseName" to "FLYING EXERCISE (TEST)",
                        "historySize" to 3,
                        "volumes" to listOf(3000.0, 3000.0, 3300.0),
                        "recentVolumes" to listOf(3000.0, 3300.0),
                        "initialVolume" to 3000.0,
                        "finalVolume" to 3300.0,
                        "percentageChange" to 0.10,
                        "recentComments" to listOf(
                            "easy reps, felt full of energy",
                            "tired and lacked motivation"
                        )
                    )
                )
            )
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

