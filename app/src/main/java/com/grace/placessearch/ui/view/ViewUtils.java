package com.grace.placessearch.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.customtabs.CustomTabsService;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import java.util.List;
import java.util.Locale;

/**
 * Created by vicsonvictor on 4/23/18.
 */

public class ViewUtils {

    /**
     * Makes a substring of a string bold.
     *
     * @param text       Full text
     * @param textToBold Text you want to make bold
     * @return String with bold substring
     */
    public static SpannableStringBuilder applyBoldStyleToText(String text, String textToBold) {
        if (text == null) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (textToBold == null || textToBold.trim().isEmpty()) {
            return builder.append(text);
        }

        // for counting start/end indexes
        String testText = text.toLowerCase(Locale.US);
        String testTextToBold = textToBold.toLowerCase(Locale.US);
        int startingIndex = testText.indexOf(testTextToBold);
        int endingIndex = startingIndex + testTextToBold.length();

        if (startingIndex < 0 || endingIndex < 0) {
            return builder.append(text);
        } else if (startingIndex >= 0 && endingIndex >= 0) {
            builder.append(text);
            builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
        }

        return builder;
    }

    /**
     * Returns a boolean which indicates whether Chrome custom tab is supported or not
     * on the current device.
     */
    public static boolean isChromeTabSupported(Context context) {

        // Get default VIEW intent handler that can view a web url.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));

        // Get all apps that can handle VIEW intents.
        PackageManager pm = context.getPackageManager();

        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);

        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                return true;
            }
        }

        return false;
    }

}
