package com.example.athlekin.data

import com.example.athlekin.datasource.GeminiDataSource
import com.example.athlekin.model.PlateauAnalysis
import javax.inject.Inject

class GeminiRepo @Inject constructor(
    private val geminiDataSource : GeminiDataSource
)  {

    // accepts an analysisObject from previous exercies and sends it to geminiDataSource
    suspend fun generatePlateauMessage(analysisObject : PlateauAnalysis) : String? {
        return geminiDataSource.generateTextFromPrompt(analysisObject)

    }

}