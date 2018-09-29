package com.zhenl.payhook

import android.os.Environment
import java.io.File

/**
 * Created by lin on 2018/9/29.
 */
object Common {

    val APP_DIR = "com.zhenl.payhook"
    val MOD_PREFS = "settings"

    val APP_DIR_PATH: String by lazy {
        Environment.getExternalStorageDirectory().absolutePath + File.separator + APP_DIR + File.separator
    }
}