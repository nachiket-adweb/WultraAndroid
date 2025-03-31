package com.android.wultrausage.screens

import RetrofitService
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.wultrausage.services.GlobalValidationObs
import com.android.wultrausage.services.WultraUtils
import com.android.wultrausage.services.formatJson
import com.wultra.android.sslpinning.CertStore
import com.wultra.android.sslpinning.UpdateMode
import com.wultra.android.sslpinning.UpdateResult
import com.wultra.android.sslpinning.UpdateType
import com.wultra.android.sslpinning.integration.DefaultUpdateObserver
import com.wultra.android.sslpinning.integration.powerauth.powerAuthCertStore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException
import javax.net.ssl.SSLHandshakeException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetrofitScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var json by remember { mutableStateOf("") }
    var infoText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val wultraContext = LocalContext.current
    val wultraCertStore = CertStore.powerAuthCertStore(
        configuration = WultraUtils.configuration,
        wultraContext,
    )
    wultraCertStore.addValidationObserver(GlobalValidationObs(wultraCertStore))

    wultraCertStore.update(
        UpdateMode.DEFAULT,
        object : DefaultUpdateObserver() {
            override fun continueExecution() {
                // Certstore is likely up-to-date, you can resume execution of your code.
                infoText = "info:: Wultra continueExecution"
                Log.i("RetrofitScreen()", infoText)
            }

            override fun handleFailedUpdate(type: UpdateType, result: UpdateResult) {
                // There was an error during the update, present an error to the user.
                infoText = "info:: Wultra handleFailedUpdate"
                Log.e(
                    "RetrofitScreen()",
                    "$infoText\n....UpdateType -> $type\n....UpdateResult -> $result",
                )
            }

            override fun onUpdateStarted(type: UpdateType) {
                super.onUpdateStarted(type)
                infoText = "info:: Wultra onUpdateStarted"
                Log.i(
                    "RetrofitScreen()",
                    "$infoText\n....UpdateType -> $type",
                )
            }

            override fun onUpdateFinished(type: UpdateType, result: UpdateResult) {
                super.onUpdateFinished(type, result)
                infoText = "info:: Wultra onUpdateFinished"
                Log.i(
                    "RetrofitScreen()",
                    "$infoText\n....UpdateType -> $type\n....UpdateResult -> $result",
                )
            }
        },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.surface,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text("Wultra - Retrofit")
                }
            )
        },

        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(
                        fraction = 1f
                    ),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    ElevatedButton(
                        onClick = {
                            scope.launch(IO) {
                                Log.i("RetrofitScreen()", "Calling API")
                                try {
                                    infoText = "Text From update method"
                                    val apiService = RetrofitService.create(wultraCertStore)
                                    json = formatJson(apiService.getJsonText().body().toString())
                                    Log.i("RetrofitScreen()", "RESPONSE GOTT ::: $json")
                                } catch (e: SSLHandshakeException) {
                                    infoText = "info:: SSLHandshake Exception Occurred"
                                    json = "CATCH SSL GOTT ::: $json"
                                    Log.e("RetrofitScreen()", json)
                                    e.printStackTrace()
                                } catch (e: IOException) {
                                    infoText = "info:: IO Exception Occurred"
                                    json = "Error: ${e.localizedMessage}"
                                    Log.e("RetrofitScreen()", json)
                                    e.printStackTrace()
                                } catch (e: Exception) {
                                    infoText = "info:: Exception Occurred"
                                    json = "Error: ${e.localizedMessage}"
                                    Log.e("RetrofitScreen()", json)
                                    e.printStackTrace()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Text("Fetch API")
                    }
                    Text(
                        infoText,
                        modifier = Modifier.padding(10.dp),
                    )
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            json,
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                        )
                    }
                }
            }
        },
    )
}