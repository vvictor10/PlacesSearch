package com.grace.placessearch.common.ui.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import com.grace.placessearch.R

class LoadingIndicatorView : RelativeLayout {

    private val animationDuration = 600
    private val interpolator = LinearInterpolator()

    private var square1: View
    private var square2: View
    private var square3: View

    lateinit var loadingIndicatorAnimation: AnimatorSet

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingIndicatorView, 0, 0)

        var smallView : Boolean
        var shade : Int
        try {
            shade = ta.getInt(R.styleable.LoadingIndicatorView_shade, LIGHT)
            smallView = ta.getBoolean(R.styleable.LoadingIndicatorView_smallView, false)
        } finally {
            ta.recycle()
        }

        if (smallView) {
            addView(LayoutInflater.from(context).inflate(R.layout.loading_indicator_small, this, false))
            shade = TRANSPARENT
        } else {
            addView(LayoutInflater.from(context).inflate(R.layout.loading_indicator, this, false))
        }

        square1 = findViewById(R.id.square_1)
        square2 = findViewById(R.id.square_2)
        square3 = findViewById(R.id.square_3)

        isClickable = true

        setShade(shade)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility == View.VISIBLE) {
            true -> loadingIndicatorAnimation.start()
            false -> loadingIndicatorAnimation.cancel()
        }
    }

    private fun setShade(colorType: Int) {
        setSquareColor(R.color.gray_3)
        background = when (colorType) {
            DARK -> ContextCompat.getDrawable(context, R.drawable.dark_background)
            else -> ContextCompat.getDrawable(context, R.drawable.light_background)
        }
        setupAnimator()
    }

    private fun setSquareColor(colorResId: Int) {
        square1.setBackgroundResource(colorResId)
        square2.setBackgroundResource(colorResId)
        square3.setBackgroundResource(colorResId)
    }

    private fun setupAnimator() {

        loadingIndicatorAnimation = AnimatorSet()
        val square1Animator = fadeAnimator(square1, 0.2f, 1f)
        val square2Animator = fadeAnimator(square2, 0.2f, 1f)
        square2Animator.startDelay = (animationDuration / 2).toLong()
        val square3Animator = fadeAnimator(square3, 1f, 0.2f)
        loadingIndicatorAnimation.playTogether(square1Animator, square2Animator, square3Animator)
    }

    private fun fadeAnimator(view: View, startAlpha: Float, endAlpha: Float): Animator {
        val objectAnimator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha)
        objectAnimator.duration = animationDuration.toLong()
        objectAnimator.repeatCount = ValueAnimator.INFINITE
        objectAnimator.repeatMode = ValueAnimator.REVERSE
        objectAnimator.interpolator = interpolator
        return objectAnimator
    }

    companion object {
        const val DARK = 0
        const val LIGHT = 1
        const val TRANSPARENT = 7
    }
}