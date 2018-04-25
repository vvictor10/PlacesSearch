package com.grace.placessearch.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.grace.placessearch.R;

public class LoadingIndicatorView extends RelativeLayout {

    public static final int DARK = 0;
    public static final int LIGHT = 1;
    public static final int TRANSPARENT = 7;
    private static final int animationDuration = 600;
    private final LinearInterpolator interpolator = new LinearInterpolator();
    private View square1;
    private View square2;
    private View square3;

    private AnimatorSet loadingIndicatorAnimation;

    public LoadingIndicatorView(Context context) {
        this(context, null, 0);
    }

    public LoadingIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingIndicatorView, 0, 0);

        boolean smallView = false;
        int shade = LIGHT;
        try {
            shade = ta.getInt(R.styleable.LoadingIndicatorView_shade, LIGHT);
            smallView = ta.getBoolean(R.styleable.LoadingIndicatorView_smallView, false);
        } finally {
            ta.recycle();
        }

        if (smallView) {
            addView(LayoutInflater.from(context).inflate(R.layout.loading_indicator_small, this, false));
            shade = TRANSPARENT;
        } else {
            addView(LayoutInflater.from(context).inflate(R.layout.loading_indicator, this, false));
        }
        square1 = findViewById(R.id.square_1);
        square2 = findViewById(R.id.square_2);
        square3 = findViewById(R.id.square_3);
        setClickable(true);

        setShade(shade);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            loadingIndicatorAnimation.start();
        } else {
            loadingIndicatorAnimation.cancel();
        }
    }

    public void setShade(int colorType) {
        setSquareColor(R.color.gray_3);
        switch (colorType) {
            case DARK:
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.dark_background));
                break;

            default:
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.light_background));
        }
        setupAnimator();
    }

    private void setSquareColor(int colorResId) {
        square1.setBackgroundResource(colorResId);
        square2.setBackgroundResource(colorResId);
        square3.setBackgroundResource(colorResId);
    }

    private void setupAnimator() {

        loadingIndicatorAnimation = new AnimatorSet();
        Animator square1Animator = fadeAnimator(square1, 0.2f, 1f);
        Animator square2Animator = fadeAnimator(square2, 0.2f, 1f);
        square2Animator.setStartDelay(animationDuration / 2);
        Animator square3Animator = fadeAnimator(square3, 1f, 0.2f);
        loadingIndicatorAnimation.playTogether(square1Animator, square2Animator, square3Animator);
    }

    private Animator fadeAnimator(View view, float startAlpha, float endAlpha) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha);
        objectAnimator.setDuration(animationDuration);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setInterpolator(interpolator);
        return objectAnimator;
    }
}
