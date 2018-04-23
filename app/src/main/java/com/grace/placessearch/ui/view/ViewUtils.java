package com.grace.placessearch.ui.view;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

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

}
