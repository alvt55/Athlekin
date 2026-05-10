package com.example.athlekin.ui.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.athlekin.model.PlateauAnalysis
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.generationConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch

data class TestResult(
    val modelName: String,
    val exerciseName: String,
    val result: String,
    val isError: Boolean = false
)

@OptIn(PublicPreviewAPI::class)
@Composable
fun PlateauTestScreen() {
    val coroutineScope = rememberCoroutineScope()
    val testResults = remember { mutableStateListOf<TestResult>() }
    var isRunning by remember { mutableStateOf(false) }

    val analyses = remember {
        listOf(
            // Data mapped from DataSeeder.kt
            PlateauAnalysis(
                exerciseName = "Bench Press",
                historySize = 3,
                volumes = listOf(3000.0, 3150.0, 3450.0),
                recentVolumes = listOf(3000.0, 3150.0, 3450.0),
                initialVolume = 3000.0,
                finalVolume = 3450.0,
                percentageChange = 0.15,
                recentComments = listOf("Felt very strong, good progression and form is perfect", "", "")
            ),
            PlateauAnalysis(
                exerciseName = "Squat",
                historySize = 3,
                volumes = listOf(3000.0, 3000.0, 3000.0),
                recentVolumes = listOf(3000.0, 3000.0, 3000.0),
                initialVolume = 3000.0,
                finalVolume = 3000.0,
                percentageChange = 0.0,
                recentComments = listOf("", "", "i hate this, losing motivation i want to quit, maybe i should stop")
            ),
            PlateauAnalysis(
                exerciseName = "Overhead Press",
                historySize = 3,
                volumes = listOf(3000.0, 3030.0, 3060.0),
                recentVolumes = listOf(3000.0, 3030.0, 3060.0),
                initialVolume = 3000.0,
                finalVolume = 3060.0,
                percentageChange = 0.02,
                recentComments = listOf("", "", "not making to much progress, losing hope, also feel like i might injure myself")
            ),
            PlateauAnalysis(
                exerciseName = "Deadlift",
                historySize = 3,
                volumes = listOf(1500.0, 1400.0, 1300.0),
                recentVolumes = listOf(1500.0, 1400.0, 1300.0),
                initialVolume = 1500.0,
                finalVolume = 1300.0,
                percentageChange = -0.133,
                recentComments = listOf("feel good about the pullups, good muscle contractions", "", "feeling discouraged, getting weaker")
            )
        )
    }

    val modelsToTest = listOf(
        "gemini-3-flash-preview",
        "gemini-3.1-flash-lite",
        "gemini-2.5-pro",
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Plateau Model Comparison Lab", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                isRunning = true
                testResults.clear()
                coroutineScope.launch {
                    runModelComparison(modelsToTest, analyses) { result ->
                        testResults.add(result)
                    }
                    isRunning = false
                }
            },
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRunning) "Benchmarking Models..." else "Run Comparison Tests")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(testResults) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isError) MaterialTheme.colorScheme.errorContainer 
                                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.modelName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Text(item.exerciseName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(item.result, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(PublicPreviewAPI::class)
private suspend fun runModelComparison(
    models: List<String>,
    analyses: List<PlateauAnalysis>,
    onResult: (TestResult) -> Unit
) {
    val gson = Gson()
    for (modelName in models) {
        val model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = modelName,
                generationConfig = generationConfig {
                    temperature = 1.0f
                }
            )

        for (analysis in analyses) {
            val prompt = """
<role>
You are an encouraging Fitness Coach integrated into an Android workout tracking application. Your persona is professional, motivating, and grounded in exercise science. You provide insights based on progression data without being discouraging or overly "robotic."
</role>

<instructions>
1. **Analyze**: Review the provided `PlateauAnalysis` object.
2. **Evaluate**: Determine the progress type: Strong (>5%), Soft Plateau (0.1%-4.9%), Hard Plateau (0%), or Regression (negative).
3. **Draft**: Create a 1-3 sentence response following the mandatory structure.
4. **Self-Critique**: Ensure the tone is supportive. For Strong Progress, encourage the user to continue their current trajectory safely. For Plateaus, suggest variety (tempo, stance, rest) to spark new growth. For Regressions, emphasize recovery and form.
</instructions>

<constraints>
- **Length**: Maximum 3 sentences.
- **Output Verbosity**: 60-80 words (Max 100 tokens).
- **Format**: Plain sentence form only. No headers or bullet points.
- **Content**: Must include:
    1. A summary of the current plateau/trend and the data-driven "why."
    2. Specific advice on how to improve or maintain momentum (e.g., progressive overload, tempo, or recovery) safely.
    3. A final motivating sentence that builds confidence.
- **Fallback**: If analysis is impossible, return: "Keep up the good work! Remember to keep pushing hard on ${analysis.exerciseName}."
</constraints>

<few_shot_examples>
Example 1 (Strong Progress):
Input: PlateauAnalysis(exerciseName="Bench Press", historySize=8, volumes=[1200.0, 1350.0, 1400.0, 1500.0, 1600.0, 1650.0, 1700.0, 1800.0], recentVolumes=[1600.0, 1700.0, 1800.0], initialVolume=1600.0, finalVolume=1800.0, percentageChange=0.125, recentComments=["Felt strong", "Good control", "Easy reps"])
Output: Your Bench Press volume has surged by 12.5% over the last few sessions, showing that your current training intensity is highly effective. You should look to maintain this momentum by continuing to add weight or reps as you feel capable, ensuring you prioritize your lifting form to keep the progress sustainable. You are clearly in a zone of high adaptation, so keep that same fire burning as you head into your next session!

Example 2 (Hard Plateau):
Input: PlateauAnalysis(exerciseName="Squat", historySize=6, volumes=[2000.0, 2000.0, 2000.0, 2000.0, 2000.0, 2000.0], recentVolumes=[2000.0, 2000.0, 2000.0], initialVolume=2000.0, finalVolume=2000.0, percentageChange=0.0, recentComments=["Felt heavy", "Same as last time", "No progress"])
Output: Your Squat volume has remained identical at 2000.0 for several sessions, indicating a hard plateau where your body has fully adapted to the current stress. To break this cycle, try introducing a "pause" at the bottom of your reps or slightly adjusting your stance to stimulate different muscle fibers without needing to force a higher weight immediately. This stagnation is just a temporary signal to pivot your strategy, and you have the foundation to break through it soon!

Example 3 (Soft Plateau):
Input: PlateauAnalysis(exerciseName="Overhead Press", historySize=5, volumes=[480.0, 490.0, 492.0, 493.0, 494.0], recentVolumes=[492.0, 493.0, 494.0], initialVolume=492.0, finalVolume=494.0, percentageChange=0.004, recentComments=["Slight improvement", "Still tough", "Slow progress"])
Output: With a marginal gain of only 0.4%, your Overhead Press is in a soft plateau where the current routine is providing just enough stimulus to maintain but not significantly grow. You can spark fresh progress by experimenting with minor changes like shorter rest periods or a different rep range to challenge your central nervous system in a new way. Every small adjustment brings you one step closer to your next big breakthrough, so keep refining the process!

Example 4 (Regression):
Input: PlateauAnalysis(exerciseName="Deadlift", historySize=7, volumes=[3200.0, 3100.0, 3000.0, 2950.0, 2900.0, 2850.0, 2800.0], recentVolumes=[2900.0, 2850.0, 2800.0], initialVolume=2900.0, finalVolume=2800.0, percentageChange=-0.034, recentComments=["Felt fatigued", "Grip slipping", "Low energy"])
Output: Your Deadlift volume has declined by 3.4%, a sign of a regression plateau that often stems from accumulated fatigue or temporary recovery gaps. It is best to prioritize quality over quantity right now by focusing on technical cues or slightly reducing intensity for a week to allow your body to bounce back. Remember that even the strongest athletes have down periods, and taking a smart step back today is exactly what will allow you to leap forward tomorrow!
</few_shot_examples>

<context>
${gson.toJson(analysis)}
</context>

<task>
Based on the provided analysis object, generate the coaching response.
</task>
            """.trimIndent()

            try {
                val response = model.generateContent(prompt)
                onResult(TestResult(modelName, analysis.exerciseName, response.text ?: "No response"))
            } catch (e: Exception) {
                onResult(TestResult(modelName, analysis.exerciseName, e.message ?: "Unknown error", true))
            }
        }
    }
}
