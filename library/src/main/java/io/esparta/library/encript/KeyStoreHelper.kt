package io.esparta.library.encript

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import timber.log.Timber
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.security.auth.x500.X500Principal


/**
 *
 * Created by gmribas on 15/11/17.
 * Copied from https://medium.com/@ali.muzaffar/securing-sharedpreferences-in-android-a21883a9cbf8
 */
@Suppress("MemberVisibilityCanPrivate")
object KeyStoreHelper {

    const val KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
    const val TYPE_RSA = "RSA"
    const val PADDING_TYPE = "PKCS1Padding"
    const val BLOCKING_MODE = "NONE"

    const val SIGNATURE_SHA256withRSA = "SHA256withRSA"
    const val SIGNATURE_SHA512withRSA = "SHA512withRSA"

    /**
     * Creates a public and private key and stores it using the Android Key
     * Store, so that only this application will be able to access the keys.
     */
    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, KeyStoreNotSupportedException::class)
    fun createKeys(context: Context, alias: String) {
        if (!isSigningKey(alias)) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> createKeysM(alias, false)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> createKeysJBMR2(context, alias)
                else -> throw KeyStoreNotSupportedException()
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun createKeysJBMR2(context: Context, alias: String) {

        val start = GregorianCalendar()
        val end = GregorianCalendar()
        end.add(Calendar.YEAR, 30)

        @Suppress("DEPRECATION")
        val spec = KeyPairGeneratorSpec.Builder(context)
                // You'll use the alias later to retrieve the key. It's a key
                // for the key!
                .setAlias(alias)
                .setSubject(X500Principal("CN=" + alias))
                .setSerialNumber(BigInteger.valueOf(Math.abs(alias.hashCode()).toLong()))
                // Date range of validity for the generated pair.
                .setStartDate(start.time).setEndDate(end.time)
                .build()

        val kpGenerator = KeyPairGenerator.getInstance(
                TYPE_RSA,
                KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
        kpGenerator.initialize(spec)
        val kp = kpGenerator.generateKeyPair()
        Timber.d("Public Key is: " + kp.public.toString())
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun createKeysM(alias: String, requireAuth: Boolean) {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
            keyPairGenerator.initialize(
                    KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                            .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(1024, F4))
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setDigests(KeyProperties.DIGEST_SHA256,
                                    KeyProperties.DIGEST_SHA384,
                                    KeyProperties.DIGEST_SHA512)
                            // Only permit the private key to be used if the user authenticated
                            // within the last five minutes.
                            .setUserAuthenticationRequired(requireAuth)
                            .build())
            val keyPair = keyPairGenerator.generateKeyPair()
            Timber.d("Public Key is: " + keyPair.public.toString())

        } catch (e: NoSuchProviderException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }
    }

    /**
     * JBMR2+ If Key with the default alias exists, returns true, else false.
     * on pre-JBMR2 returns true always.
     */
    private fun isSigningKey(alias: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                keyStore.load(null)
                keyStore.containsAlias(alias)
            } catch (e: Exception) {
                Timber.e(e.message, e)
                false
            }

        } else {
            false
        }
    }

    /**
     * Returns the private key signature on JBMR2+ or else null.
     */
    fun getSigningKey(alias: String): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val cert = getPrivateKeyEntry(alias)?.certificate ?: return null
            return Base64.encodeToString(cert.encoded, Base64.NO_WRAP)
        } else {
            return null
        }
    }

    private fun getPrivateKeyEntry(alias: String): KeyStore.PrivateKeyEntry? {
        try {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
            ks.load(null)
            val entry = ks.getEntry(alias, null)

            if (entry == null) {
                Timber.w("No key found under alias: " + alias)
                Timber.w("Exiting signData()...")
                return null
            }

            if (entry !is KeyStore.PrivateKeyEntry) {
                Timber.w("Not an instance of a PrivateKeyEntry")
                Timber.w("Exiting signData()...")
                return null
            }
            return entry
        } catch (e: Exception) {
            Timber.e(e.message, e)
            return null
        }
    }

    @Throws(RuntimeException::class)
    fun encrypt(alias: String, plaintext: String): String {
        try {
            val publicKey = getPrivateKeyEntry(alias)?.certificate?.publicKey
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray()), Base64.NO_WRAP)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(RuntimeException::class)
    fun decrypt(alias: String, cipherText: String): String {
        try {
            val privateKey = getPrivateKeyEntry(alias)?.privateKey
            val cipher = getCipher()
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return String(cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP)))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class)
    private fun getCipher(): Cipher =
            Cipher.getInstance(String.format("%s/%s/%s", TYPE_RSA, BLOCKING_MODE, PADDING_TYPE))
}