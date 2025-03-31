package com.android.wultrausage.services

import android.util.Log
import com.wultra.android.sslpinning.CertStore
import com.wultra.android.sslpinning.UpdateMode
import com.wultra.android.sslpinning.UpdateResult
import com.wultra.android.sslpinning.UpdateType
import com.wultra.android.sslpinning.ValidationObserver
import com.wultra.android.sslpinning.integration.DefaultUpdateObserver

class GlobalValidationObs(
    private val certStore: CertStore,
) : ValidationObserver {
    override fun onValidationEmpty(commonName: String) {
        Log.e(
            "GlobalValidationObserver",
            "Validation empty: No certificates in store for common Name: $commonName.",
        )
        forceUpdateFingerprints()
    }

    override fun onValidationTrusted(commonName: String) {
        Log.i(
            "GlobalValidationObserver",
            "Validation trusted: $commonName",
        )
    }

    override fun onValidationUntrusted(commonName: String) {
        Log.e(
            "GlobalValidationObserver",
            "Validation untrusted: $commonName",
        )
        forceUpdateFingerprints()
    }

    private fun forceUpdateFingerprints() {
        certStore.update(
            UpdateMode.FORCED,
            object : DefaultUpdateObserver() {
                override fun continueExecution() {
                    Log.i(
                        "GlobalValidationObserver",
                        "Forced update successful",
                    )
                }

                override fun handleFailedUpdate(
                    type: UpdateType,
                    result: UpdateResult
                ) {
                    Log.e(
                        "GlobalValidationObserver",
                        "Forced update failed: $result",
                    )
                }
            },
        )
    }
}