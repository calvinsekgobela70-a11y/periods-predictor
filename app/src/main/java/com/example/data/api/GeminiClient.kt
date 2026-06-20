package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Gracefully checks if key is configured and valid
    fun isKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrEmpty() && key != "MY_GEMINI_API_KEY" && !key.startsWith("YOUR_")
    }

    suspend fun generateHealthInsight(symptoms: List<String>, moods: List<String>, flow: String?): String = withContext(Dispatchers.IO) {
        if (!isKeyConfigured()) {
            return@withContext getOfflineFallbackInsight(symptoms, moods)
        }

        val prompt = """
            You are Aura, a supportive, warm, and highly professional female cycle health wellness companion.
            The user is tracking her period and has logged the following details for her current phase:
            - Logged symptoms: ${symptoms.joinToString(", ")}
            - Logged moods: ${moods.joinToString(", ")}
            - Bleeding flow intensity: ${flow ?: "None / Not logged"}

            Generate a highly comforting, soothing, personalized wellness insight (max 4 sentences).
            - Keep the tone super friendly, warm, empathetic, and cozy.
            - Provide 1 clear, actionable natural self-care tip (e.g., chamomile tea, stretching, hot compress, rest).
            - Always reassure her that her body is doing amazing work.
            - Do not include clinical warnings or cold medical disclaimers in the text itself.
            - Speak directly to her as a supportive friend.
        """.trimIndent()

        // Construct raw JSON body for generateContent
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestJson = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": ${escapeJsonString(prompt)}
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("$BASE_URL/$MODEL_NAME:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
            .post(requestJson.toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed with code: ${response.code}")
                    return@withContext getOfflineFallbackInsight(symptoms, moods)
                }

                val bodyString = response.body?.string() ?: ""
                val text = extractTextFromGeminiResponse(bodyString)
                if (text.isNotEmpty()) {
                    text
                } else {
                    getOfflineFallbackInsight(symptoms, moods)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API: ${e.message}", e)
            getOfflineFallbackInsight(symptoms, moods)
        }
    }

    private fun escapeJsonString(str: String): String {
        return "\"${str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")}\""
    }

    private fun extractTextFromGeminiResponse(jsonString: String): String {
        return try {
            val jsonAdapter = moshi.adapter(Map::class.java)
            val root = jsonAdapter.fromJson(jsonString) as? Map<*, *>
            val candidates = root?.get("candidates") as? List<*>
            val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
            val content = firstCandidate?.get("content") as? Map<*, *>
            val parts = content?.get("parts") as? List<*>
            val firstPart = parts?.firstOrNull() as? Map<*, *>
            firstPart?.get("text") as? String ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini response: ${e.message}")
            ""
        }
    }

    // Creative Offline Fallback Insight engine (ensures delightful UX even without key)
    fun getOfflineFallbackInsight(symptoms: List<String>, moods: List<String>): String {
        if (symptoms.isEmpty() && moods.isEmpty()) {
            return "Welcome, lovely soul. Tracking your daily details helps me map your cycle's beautiful rhythm. Let me know how you are feeling today to receive custom herbal, comfort, and care tips!"
        }

        // Generate tailored comforting summaries based on symptoms/moods
        val parts = mutableListOf<String>()

        if (symptoms.contains("Cramps") || symptoms.contains("Backache")) {
            parts.add("I hear that you're dealing with some physical tension. Applying a soft warm compress or heating pad to your lower abdomen can help soothe those muscles deeply. Hydrating with warm raspberry leaf or cinnamon tea works wonders too.")
        } else if (symptoms.contains("Headache") || symptoms.contains("Fatigue")) {
            parts.add("Your body is doing sacred work and channeling a lot of energy. Ensure you afford yourself a little extra rest today—dimming the lights, stretching gently, and drinking plenty of infused cucumber water can bring sweet relief.")
        } else if (symptoms.contains("Bloating") || symptoms.contains("Acne")) {
            parts.add("Feeling a little heavy or sensitive is highly common during changes in your cycle. Gentle yoga poses like Child's Pose can relieve bloating, while staying hydrated works as a beautiful natural detox.")
        }

        if (moods.contains("Sad") || moods.contains("Sensitive") || moods.contains("Anxious")) {
            parts.add("Your emotions are flowing like water, and it is completely safe to let them exist. Be extra gentle with yourself today, sweet friend. A quick offline walk in nature or light writing can ground your heart.")
        } else if (moods.contains("Energetic") || moods.contains("Happy")) {
            parts.add("Your high-vibe energy is absolutely glowing! This is a wonderful phase to tackle creative ideas, connect with people, or enjoy a refreshing workout. Carry that inner light with pride.")
        } else {
            parts.add("Your body is slowly aligning with its natural rhythms. Honor whatever energy levels you have today. You are doing beautiful and steady.")
        }

        parts.add("Take a deep breath and remember: you are cared for, you are beautiful, and this phase is just a natural passing cloud. 🌸")

        return parts.joinToString(" ")
    }
}
