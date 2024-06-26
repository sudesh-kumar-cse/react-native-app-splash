package com.appsplash

/**
 * App Splash
 * Author:Sudesh Kumar
 * GitHub:https://github.com/sudesh-kumar-cse
 */

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import java.lang.ref.WeakReference

object AppSplash {
  private var mSplashDialog: Dialog? = null
  private var mActivity: WeakReference<Activity>? = null


  fun show(
    activity: Activity?,
    themeResId: Int,
    fullScreen: Boolean,
    transparent: Boolean,
    imageResource: Int? = R.mipmap.ic_launcher
  ) {
    if (activity == null) return
    mActivity = WeakReference(activity)
    activity.runOnUiThread(Runnable {
      if (!activity.isFinishing) {
        mSplashDialog = Dialog(activity, themeResId)
        mSplashDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mSplashDialog!!.setContentView(R.layout.app_splash)
        mSplashDialog!!.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val _imageView: ImageView = mSplashDialog!!.findViewById(R.id._dialog_imageview)
        imageResource?.let {
          _imageView.setImageResource(it)
          val widthInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            160.toFloat(),
            activity.resources.displayMetrics
          ).toInt()
          val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            160.toFloat(),
            activity.resources.displayMetrics
          ).toInt()
          val layoutParams = _imageView.layoutParams
          layoutParams.width = widthInPixels
          layoutParams.height = heightInPixels
          _imageView.layoutParams = layoutParams
        }
        mSplashDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mSplashDialog!!.setCancelable(false)
        if (fullScreen) {
          setActivityAndroidP(mSplashDialog)
          val window: Window = mSplashDialog!!.getWindow()!!
          window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
          )
          if (SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
              it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
              it.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
          } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              // Hide the nav bar and status bar
              or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              or View.SYSTEM_UI_FLAG_FULLSCREEN)
          }

        }
        if (transparent) {
          setActivityAndroidP(mSplashDialog)
          val window: Window = mSplashDialog!!.getWindow()!!
          window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
          window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
          )
          if (SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
          }
          window.statusBarColor = Color.TRANSPARENT
          window.navigationBarColor = Color.TRANSPARENT
        }
        if (!mSplashDialog!!.isShowing) {
          mSplashDialog!!.show()
        }
      }
    })
  }

  @JvmOverloads
  fun show(activity: Activity?, themeResId: Int = 0, fullScreen: Boolean = false) {
    val resourceId: Int = if (themeResId == 0) {
      if (fullScreen) {
        R.style._SplashFullscreen
      } else {
        R.style._SplashTheme
      }
    } else {
      themeResId
    }
    show(activity, resourceId, fullScreen, !fullScreen)
  }
  

  fun hide(activity: Activity?) {
    var activity = activity
    if (activity == null) {
      if (mActivity == null) {
        return
      }
      activity = mActivity!!.get()
    }
    if (activity == null) return
    val _activity: Activity = activity
    _activity.runOnUiThread {
      if (mSplashDialog != null && mSplashDialog!!.isShowing) {
        var isDestroyed = false
        if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          isDestroyed = _activity.isDestroyed
        }
        if (mSplashDialog != null && !_activity.isFinishing && !isDestroyed) {
          mSplashDialog!!.dismiss()
        }
        mSplashDialog = null
      }
    }
  }

  private fun setActivityAndroidP(dialog: Dialog?) {
    if (SDK_INT >= 28) {
      if (dialog != null && dialog.window != null) {
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val lp = dialog.window!!.attributes
        lp.layoutInDisplayCutoutMode =
          WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        dialog.window!!.setAttributes(lp)
      }
    }
  }
}
