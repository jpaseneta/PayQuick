package com.littlepay.payquick.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "payquick_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var accessToken: String
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null).orEmpty()
        private set(value) = sharedPreferences.edit { putString(KEY_ACCESS_TOKEN, value).apply() }

    var refreshToken: String
        get() = sharedPreferences.getString(KEY_REFRESH_TOKEN, null).orEmpty()
        private set(value) = sharedPreferences.edit { putString(KEY_REFRESH_TOKEN, value) }

    fun updateTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun clearTokens() {
        sharedPreferences.edit { clear() }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
