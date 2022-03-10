package com.ingenious.documentreader.Helpers

import android.content.res.Resources
import android.util.DisplayMetrics




object UIHelper {
    fun dpToPx(dp: Float, r: Resources): Float {
        val metrics: DisplayMetrics = r.getDisplayMetrics()
        return dp * (metrics.densityDpi / 160f)
    }
}