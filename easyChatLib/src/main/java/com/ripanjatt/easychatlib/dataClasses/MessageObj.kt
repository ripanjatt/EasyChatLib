package com.ripanjatt.easychatlib.dataClasses

import android.graphics.Bitmap
import android.view.View
import java.util.*

data class MessageObj(val message: String,
                      val isIncoming: Boolean,
                      val showDP: Boolean,
                      val view: View?,
                      val hasView: Boolean,
                      val dpBitmap: Bitmap?,
                      val date: Date
)