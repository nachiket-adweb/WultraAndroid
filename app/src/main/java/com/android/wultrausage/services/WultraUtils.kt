package com.android.wultrausage.services

import android.util.Base64
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.wultra.android.sslpinning.CertStoreConfiguration
import java.net.URL

internal class WultraUtils {

    companion object {
        const val baseUrl = "https://devapi.adwebtech.com"
        const val employeeApi = "/sample/employees.json"

        private const val serviceURL =
            "https://mus.adwebtech.com:8080/app/init?appName=wultra-usage"
        private const val appPublickKey =
            "BAKuxO8eaH13Nkouc3nkgbKGs29k78pE90g6buZZmCu6Ocfj7+UM+lDYs12ztOdwDyciRmvXgO4bw3IiArgJO30="
        private val publicKey: ByteArray = Base64.decode(appPublickKey, Base64.NO_WRAP)

        val configuration =
            CertStoreConfiguration.Builder(serviceUrl = URL(serviceURL), publicKey = publicKey)
                .useChallenge(true)
                .build()
    }
}

fun formatJson(jsonString: String): String {
    return try {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonElement = JsonParser.parseString(jsonString)
        gson.toJson(jsonElement)
    } catch (e: Exception) {
        "Invalid JSON: ${e.localizedMessage}"
    }
}

