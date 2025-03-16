package com.lockscreenwallpaper

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class WallpaperModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "WallpaperModule"
    }

    @ReactMethod
    fun setLockscreenWallpaper(imageUrl: String, promise: Promise) {
        Thread {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val originalBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                val wallpaperManager = WallpaperManager.getInstance(reactApplicationContext)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(originalBitmap, null, true, WallpaperManager.FLAG_LOCK)
                } else {
                    wallpaperManager.setBitmap(originalBitmap)
                }

                Handler(Looper.getMainLooper()).post {
                    promise.resolve("Lock screen wallpaper set successfully!")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    promise.reject("ERROR", "Failed to set lockscreen wallpaper", e)
                }
            }
        }.start()
    }
}
