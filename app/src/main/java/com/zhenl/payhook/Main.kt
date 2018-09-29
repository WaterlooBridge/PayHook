package com.zhenl.payhook

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File


/**
 * Created by lin on 2018/9/28.
 */
class Main : IXposedHookLoadPackage {

    var toastHandler: ToastHandler? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if ("com.eg.android.AlipayGphone".equals(lpparam?.packageName)) {
            val sp = XSharedPreferences(File(Common.APP_DIR_PATH + Common.MOD_PREFS + ".xml"))
            if (sp.getBoolean("hookAlipay", true)) {
                hookApplication()
                hookAlipay(lpparam)
                Log.e(Common.APP_DIR, "hook Alipay success")
            }
        }
    }

    fun hookApplication() {
        XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val context = param?.args!![0] as Context
                toastHandler = ToastHandler(context)
            }
        })
    }

    fun hookAlipay(lpparam: XC_LoadPackage.LoadPackageParam?) {
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.TradeDao", lpparam?.classLoader), "insertMessageInfo", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                try {
                    if (param?.args?.size == 0)
                        return
                    var content = XposedHelpers.callMethod(param?.args!![0], "toString") as String
                    Log.e(Common.APP_DIR, content)
                    content = parse(content, "content='", "'")
                    if (content.contains("二维码收款") || content.contains("收到一笔转账")) {
                        toastHandler?.toast(content)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.ServiceDao", lpparam?.classLoader), "insertMessageInfo", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                try {
                    var content = XposedHelpers.callMethod(param?.args!![0], "toString") as String
                    Log.e(Common.APP_DIR, content)
                    content = parse(content, "extraInfo='", "'").replace("\\", "")
                    if (content.contains("收钱到账") || content.contains("收款到账")) {
                        toastHandler?.toast(content)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun parse(content: String, prefix: String, suffix: String): String {
        try {
            val i = content.indexOf(prefix) + prefix.length
            return content.substring(i, content.indexOf(suffix, i))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "error"
    }

    class ToastHandler : Handler {

        val context: Context

        constructor(context: Context) {
            this.context = context
        }

        override fun handleMessage(msg: Message?) {
            Toast.makeText(context, msg?.obj as String, Toast.LENGTH_SHORT).show()
        }

        fun toast(msg: String) {
            obtainMessage(0, msg).sendToTarget()
        }
    }
}