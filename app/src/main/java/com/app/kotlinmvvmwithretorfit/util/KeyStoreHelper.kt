package com.app.smartprocessors.util

import android.os.Build
import javax.crypto.Cipher
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.StrongBoxUnavailableException
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi


import javax.crypto.spec.GCMParameterSpec
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.security.SecureRandom


// from https://github.com/signalapp/Signal-Android/blob/e603162ee767d56fa16f56701cd29010f22ed22d/src/org/thoughtcrime/securesms/crypto/KeyStoreHelper.java

class KeyStoreHelper {
    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "com.app.smartprocessors.util.secret"
        private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
        const val KEY_SIZE = 256
        const val IV_SIZE = 12 // in AES/GCM, IV is always 12 bytes
        const val TAG_SIZE = 128 // in AES/GCM, IV is always 12 bytes


        /**
         * Encrypt the given value, with key as associatedData
         * The resulting buffer is base64 encoded and will looks as follow:
         * ------------------------------
         * |    IV   |    ciphertext    |
         * ------------------------------
         * | IV_SIZE | cipher text size |
         *
         * @return The encoded buffer containing the IV + ciphertext
         */
        @RequiresApi(Build.VERSION_CODES.M)
        fun encryptKeyValue(key: String, value: String): String {
            val secretKey = getOrCreateKeyStoreEntry()

            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)

            // cipher will automatically and securely generate IV
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            cipher.updateAAD(key.toByteArray())
            val ciphertext = cipher.doFinal(value.toByteArray())

            // the iv is appended to ciphertext
            return Base64.encodeToString(
                cipher.iv + ciphertext,
                Base64.NO_WRAP or Base64.NO_PADDING
            )
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun decryptKeyValue(key: String, ciphertext: String): ByteArray {
            val ciphertextBuffer = Base64.decode(ciphertext, Base64.NO_WRAP or Base64.NO_PADDING)
            val iv = ciphertextBuffer.take(IV_SIZE)
            val data = ciphertextBuffer.drop(IV_SIZE)

            val secretKey = getKeyStoreEntry()

            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                GCMParameterSpec(TAG_SIZE, iv.toByteArray())
            )
            cipher.updateAAD(key.toByteArray())

            return cipher.doFinal(data.toByteArray())
        }


        @RequiresApi(Build.VERSION_CODES.M)
        private fun getOrCreateKeyStoreEntry(): SecretKey {
            return if (hasKeyStoreEntry()) {
                Log.d("FlutterSecureStore", "KeyStore has entry")
                getKeyStoreEntry()
            } else {
                Log.d("FlutterSecureStore", "KeyStore has not entry")
                createKeyStoreEntry()
            }
        }

        /**
         * Provides a safe and easy to use KenGenParameterSpec with the settings.
         * Algorithm: AES
         * Block Mode: GCM
         * Padding: No Padding
         * Key Size: 256
         *
         * @return The spec for the master key with the specified keyAlias
         */
        @RequiresApi(Build.VERSION_CODES.M)
        private fun createKeyStoreEntry(): SecretKey {
            return createKeyStoreEntry(MASTER_KEY_ALIAS)
        }

        /**
         * Provides a safe and easy to use KenGenParameterSpec with the settings.
         * Algorithm: AES
         * Block Mode: GCM
         * Padding: No Padding
         * Key Size: 256
         *
         * @param keyAlias The alias for the master key
         * @return The spec for the master key with the specified keyAlias
         */
        @RequiresApi(Build.VERSION_CODES.M)
        private fun createKeyStoreEntry(keyAlias: String): SecretKey {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            val builder = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setKeySize(KEY_SIZE)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    builder.setIsStrongBoxBacked(true)
                    keyGenerator.init(builder.build())
                } catch (er: StrongBoxUnavailableException) {
                    keyGenerator.init(builder.build())
                }
            } else {
                keyGenerator.init(builder.build())
            }

            return keyGenerator.generateKey()
        }


        @RequiresApi(Build.VERSION_CODES.M)
        private fun getKeyStoreEntry(): SecretKey {
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)

            return (keyStore.getEntry(MASTER_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun hasKeyStoreEntry(): Boolean {
            val ks = KeyStore.getInstance(ANDROID_KEY_STORE)
            ks.load(null)

            return ks.containsAlias(MASTER_KEY_ALIAS)
        }
    }
}
