package com.example.athlekin.datasource

import com.example.athlekin.model.PlateauAnalysis
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.gson.Gson
import javax.inject.Inject


class GeminiDataSource @Inject constructor() {


    @OptIn(PublicPreviewAPI::class)
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .templateGenerativeModel()

    val gson = Gson()

    // returns string response from a prompt
    @OptIn(PublicPreviewAPI::class)
    suspend fun generateTextFromPrompt(analysisObj: PlateauAnalysis): String? {
        return try {
            val response = model.generateContent(
                "plateau-template-v1",
                mapOf(
                    // TEST analysis object, observing the parameters for template prompting
                    "analysis" to gson.toJson(analysisObj)

                )
            )
            response.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

