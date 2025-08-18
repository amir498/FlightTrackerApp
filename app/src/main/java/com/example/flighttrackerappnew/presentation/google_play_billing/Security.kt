package com.example.flighttrackerappnew.presentation.google_play_billing

import android.text.TextUtils
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import android.util.Base64
import kotlin.jvm.Throws

class Security {
    private val keyFactorALGORITHM = "RSA"
    private val signatureALGORITHM = "SHA1withRSA"

    @Throws(IOException::class)
    fun verifyPurchase(
        base64PublicKey: String?,
        signedData: String,
        signature: String?
    ): Boolean {

        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey) || TextUtils.isEmpty(
                signature
            )
        ) {
            return false
        }
        val key = generatePublicKey(base64PublicKey)
        return verify(key, signedData, signature)
    }

    @Throws(IOException::class)
    private fun generatePublicKey(encodedPublicKey: String?): PublicKey {

        return try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(keyFactorALGORITHM)
            keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            throw IOException("Invalid Key Specification")
        }

    }

    private fun verify(publicKey: PublicKey?, signedData: String, signature: String?): Boolean {
        val signatureBytes: ByteArray = try {
            Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {

            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(signatureALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            return signatureAlgorithm.verify(signatureBytes)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        }
        return false
    }
}