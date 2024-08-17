package io.github.skydynamic.maimaidx_android.network

import android.content.Context
import android.net.IConnectivityManager
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

object IConnectivityManagerAccess {
    val connectivityManager by lazy {
        IConnectivityManager.Stub.asInterface(
            ShizukuBinderWrapper(
                SystemServiceHelper.getSystemService(
                    Context.CONNECTIVITY_SERVICE
                )
            )
        )
    }
}