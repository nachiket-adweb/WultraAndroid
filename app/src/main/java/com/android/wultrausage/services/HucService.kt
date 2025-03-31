package com.android.wultrausage.services

import com.wultra.android.sslpinning.CertStore
import com.wultra.android.sslpinning.integration.SSLPinningIntegration
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HucService {

    private val apiURL = WultraUtils.baseUrl + WultraUtils.employeeApi

    /// HTTP URL Connection
    fun getMyJsonText(certStore: CertStore): String {
        println("inside getMyJsonText()")
        val responseData = StringBuilder()
        with(URL(apiURL).openConnection() as HttpsURLConnection) {
            requestMethod = "GET"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            connectTimeout = 10000
            readTimeout = 10000
            sslSocketFactory = SSLPinningIntegration.createSSLPinningSocketFactory(certStore)
            connect()
            println("connecting HttpsURLConnection")
            try {
                val reader = InputStreamReader(inputStream)
                reader.use { input ->
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.forEachLine { responseData.append(it.trim()) }
                }
                println("RESPONSE GOTTT >> $responseData")
            } catch (e: Exception) {
                println("Stack Tracee>>>")
                e.printStackTrace() // Log or handle the exception appropriately
            } finally {
                println("Disconnnected")
                disconnect()
            }
        }
        return responseData.toString()
    }

}





