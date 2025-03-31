import com.android.wultrausage.services.WultraUtils
import com.google.gson.JsonObject
import com.wultra.android.sslpinning.CertStore
import com.wultra.android.sslpinning.integration.SSLPinningIntegration
import com.wultra.android.sslpinning.integration.SSLPinningX509TrustManager
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Retrofit Setup
interface ApiService {
    @GET("sample/employees.json")
    suspend fun getJsonText(): Response<JsonObject>
}

object RetrofitService {
    fun create(certStore: CertStore): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(
                SSLPinningIntegration.createSSLPinningSocketFactory(certStore),
                SSLPinningX509TrustManager(certStore) // Custom TrustManager
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(WultraUtils.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}