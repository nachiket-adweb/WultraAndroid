package com.android.wultrausage.services

import android.util.Log
import com.wultra.android.sslpinning.CertStore
import com.wultra.android.sslpinning.integration.SSLPinningIntegration
import com.wultra.android.sslpinning.integration.SSLPinningX509TrustManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpService {
    private val BASE_URL = WultraUtils.baseUrl + WultraUtils.employeeApi

    fun getEmployees(certStore: CertStore): String {
        // OkHttp Setup
        val logInterceptor = HttpLoggingInterceptor()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
            .sslSocketFactory(
                SSLPinningIntegration.createSSLPinningSocketFactory(certStore),
                SSLPinningX509TrustManager(certStore)
            ).build()

        val request = Request.Builder().url(BASE_URL).build()
        var jsontext = "Empty response"
        var errortext = "Empty Error"

        Log.i("OkHttpService", "Making API call")

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    jsontext = formatJson(response.body?.string() ?: jsontext)
                    Log.i("OkHttpService", "msg $jsontext")
                } else {
                    errortext = "Error: ${response.message}"
                    Log.e("OkHttpService", "err $errortext")
                }
            }
        } catch (e: Exception) {
            errortext = "Error: ${e.localizedMessage}"
            Log.e("OkHttpService", "err $errortext")
        }

        return if (jsontext != "Empty response") jsontext else errortext
    }
}