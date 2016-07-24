package com.bowstringllp.spiderattack.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rishabhjain on 7/5/16.
 */
public class CustomFontTextView extends TextView {

    public CustomFontTextView(Context context) {
        super(context);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "creepycrawlers.ttf"));
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "creepycrawlers.ttf"));
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "creepycrawlers.ttf"));
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "creepycrawlers.ttf"));
    }
}