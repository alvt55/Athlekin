package com.example.athlekin.data

import com.example.athlekin.datasource.GeminiDataSource
import com.example.athlekin.model.PlateauAnalysis
import javax.inject.Inject

class GeminiRepo @Inject constructor(
    private val geminiDataSource : GeminiDataSource
)  {

    // accepts an analysisObject from previous exercises
    // generates a prompt tailored to that object and sends it over to the GeminiDataSource function
    suspend fun generatePlateauMessage(analysisObject : PlateauAnalysis) : String? {
        return geminiDataSource.generateTextFromPrompt(analysisObject)

    }

}