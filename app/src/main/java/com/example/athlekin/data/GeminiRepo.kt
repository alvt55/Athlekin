package com.example.athlekin.data

import com.example.athlekin.datasource.GeminiDataSource
import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import com.example.athlekin.model.PlateauAnalysis
import javax.inject.Inject

class GeminiRepo @Inject constructor(
    private val GeminiDataSource : GeminiDataSource
)  {

    suspend fun generatePlateauMessage(analysisObject : PlateauAnalysis) : String{
        return "test"
    }

}