package expo.modules.otpverify

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.CodedException
import expo.modules.kotlin.exception.toCodedException

class ExpoOtpVerifyModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoOtpVerify")

    val mReceiver = OtpBroadcastReceiver(appContext, this@ExpoOtpVerifyModule)
    var isReceiverRegistered = false
    val tag = ExpoOtpVerifyModule::class.java.simpleName

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiverIfNecessary(receiver: OtpBroadcastReceiver) {
      if (appContext.currentActivity == null) {
        return
      }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              appContext.currentActivity?.registerReceiver(receiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), SmsRetriever.SEND_PERMISSION, null, Context.RECEIVER_EXPORTED)
            } else {
              appContext.currentActivity?.registerReceiver(receiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
            }
            isReceiverRegistered = true
            Log.d(tag, "Receiver registered")
        } catch (e: Exception) {
          e.printStackTrace();
        }
    }

    fun unregisterReceiver(receiver: OtpBroadcastReceiver) {
      if (isReceiverRegistered) {
        try {
          appContext.currentActivity?.unregisterReceiver(receiver)
          Log.d(tag, "Receiver unregistered")
          isReceiverRegistered = false
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    OnCreate {
      Log.d("SMS", "onCreate")
      registerReceiverIfNecessary(mReceiver)
    }

    OnDestroy {
      Log.d("SMS", "OnDestroy")
      unregisterReceiver(mReceiver)
    }

    OnActivityDestroys {
      Log.d("SMS", "OnActivityDestroys")
      unregisterReceiver(mReceiver)
    }

    Function("getHash") {
      val helper = AppSignatureHelper(appContext.reactContext)
      val signature = helper.getAppSignatures()
      return@Function signature
    }

    AsyncFunction("getOtp") { promise: Promise ->
      val client = appContext.reactContext?.let { SmsRetriever.getClient(it) }
      if (client == null) {
        promise.reject(CodedException())
        return@AsyncFunction false
      }
      val task = client.startSmsRetriever()
      task.addOnCanceledListener {
        Log.i(tag, "sms listener cancelled")
      }
      task.addOnCompleteListener {
        Log.i(tag, "sms listener complete")
      }
      task.addOnSuccessListener {
        Log.i(tag, "started sms listener")
        promise.resolve(true)
      }
      task.addOnFailureListener {
        e ->
        Log.i(tag, "Could not start sms listener $e")
        promise.reject(e.toCodedException())
      }
    }

    Events("onOtpReceived")

  }
}
