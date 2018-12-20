package com.grace.placessearch.common.ui.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Typeface
import android.net.Uri
import android.support.customtabs.CustomTabsService
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import java.util.Locale

/**
 * Created by vicsonvictor on 4/23/18.
 */
object ViewUtils {
    /**
     * Makes a substring of a string bold.
     *
     * @param text       Full text
     * @param textToBold Text you want to make bold
     * @return String with bold substring
     */
    fun applyBoldStyleToText(text: String?, textToBold: String?): SpannableStringBuilder? {
        if (text == null) {
            return null
        }

        val builder = SpannableStringBuilder()

        if (textToBold == null || textToBold.trim { it <= ' ' }.isEmpty()) {
            return builder.append(text)
        }

        // for counting start/end indexes
        val testText = text.toLowerCase(Locale.US)
        val testTextToBold = textToBold.toLowerCase(Locale.US)
        val startingIndex = testText.indexOf(testTextToBold)
        val endingIndex = startingIndex + testTextToBold.length

        when {
            (startingIndex < 0 || endingIndex < 0) -> return builder.append(text)
            (startingIndex >= 0 && endingIndex >= 0) ->  {
                builder.append(text)
                builder.setSpan(StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0)
            }
        }

        return builder
    }

    /**
     * Returns a boolean which indicates whether Chrome custom tab is supported or not
     * on the current device.
     */
    fun isChromeTabSupported(context: Context): Boolean {

        // Get default VIEW intent handler that can view a web url.
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))

        // Get all apps that can handle VIEW intents.
        val pm = context.packageManager

        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)

        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            if (pm.resolveService(serviceIntent, 0) != null) {
                return true
            }
        }

        return false
    }

}
