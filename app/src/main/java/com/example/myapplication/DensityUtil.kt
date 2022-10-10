package com.example.myapplication

import android.content.res.Resources

class DensityUtil {

    companion object {
        fun dp2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

}
