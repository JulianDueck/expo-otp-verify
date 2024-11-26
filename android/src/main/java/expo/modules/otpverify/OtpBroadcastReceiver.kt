package expo.modules.otpverify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status;
import expo.modules.kotlin.AppContext

class OtpBroadcastReceiver(context: AppContext?, expoOtpVerifyModule: ExpoOtpVerifyModule) : BroadcastReceiver() {

    private val mContext = context
    private val expoModule = expoOtpVerifyModule

    private fun receiveMessage(message: String?) {
        if (mContext == null || !mContext.hasActiveReactInstance) {
            return
        }
        expoModule.sendEvent("onOtpReceived", bundleOf("message" to message))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val o = intent?.action
        if (SmsRetriever.SMS_RETRIEVED_ACTION == o) {
            val extras = intent.extras ?: return
            val status: Status = extras.get(SmsRetriever.EXTRA_STATUS) as Status ?: return
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    if (message != null) {
                        Log.d("SMS", message)
                    }
                    receiveMessage(message)
                }
                CommonStatusCodes.TIMEOUT -> {
                    Log.d("SMS", "Timeout error")
                    expoModule.sendEvent("onOtpReceived", bundleOf("message" to "Timeout Error."))
                }
            }
        }
    }
}