package expo.modules.otpverify
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class AppSignatureHelper(context: Context?) : ContextWrapper(context) {

    private val TAG = AppSignatureHelper::class.java.simpleName
    private val HASH_TYPE = "SHA-256"
    private val NUM_HASHED_BYTES = 9
    private val NUM_BASE64_CHAR = 11


    fun getAppSignatures(): ArrayList<String> {
        val appCodes = ArrayList<String>()

        try {
            val packageName = packageName
            val packageManager = packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signature = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                ).signingInfo

                val hash = hash(packageName, signature.toString())
                hash?.let { appCodes.add(it) }

            } else {
                val signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
                if (signatures != null) {
                    for (signature in signatures) {
                        val hash = hash(packageName, signature.toCharsString())
                        hash?.let { appCodes.add(it) }
                    }
                }
            }

        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to find package to obtain hash.", e)
        }
        return appCodes
    }


    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        return try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // truncate into NUM_HASHED_BYTES
            hashSignature = hashSignature.copyOfRange(0, NUM_HASHED_BYTES)

            // encode into Base64
            var base64Hash = Base64.encodeToString(
                hashSignature,
                Base64.NO_PADDING or Base64.NO_WRAP
            )
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            Log.d(TAG, "pkg: $packageName -- hash: $base64Hash")
            base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "hash:NoSuchAlgorithm", e)
            null
        }
    }
}