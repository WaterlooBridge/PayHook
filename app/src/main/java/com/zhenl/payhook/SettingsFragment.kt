package com.zhenl.payhook

import android.os.Bundle
import android.preference.PreferenceFragment

/**
 * Created by lin on 2018/9/29.
 */
class SettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = Common.MOD_PREFS
        addPreferencesFromResource(R.xml.pref_setting)
    }
}