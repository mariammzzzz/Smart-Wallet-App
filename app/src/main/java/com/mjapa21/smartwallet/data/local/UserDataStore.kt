package com.mjapa21.smartwallet.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

/**
 * A DataStore<Preferences> is a singleton per file name — this extension property
 * guarantees that even if multiple classes reference Context.userDataStore, they
 * all get the same underlying instance. Keep this file the ONLY place that creates it.
 */
val Context.userDataStore by preferencesDataStore(name = "user_preferences")
