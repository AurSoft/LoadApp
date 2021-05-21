package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progress = 0.0f
    private val rectF = RectF()

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new -> }

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
        strokeWidth = 0f
        textAlign = Paint.Align.CENTER
    }

    init {
        initValueAnimator()
    }

    private fun initValueAnimator() {
        valueAnimator.duration = 1000
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            if (progress == widthSize.toFloat()) {
                progress = 0f
                buttonState = ButtonState.Completed
                isEnabled = true
            }
            invalidate()
            requestLayout()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        isEnabled = false
        buttonState = ButtonState.Clicked
        valueAnimator.start()
        buttonState = ButtonState.Loading
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBackgroundColor(context.getColor(R.color.colorPrimary))
        drawProgressBar(canvas, progress)
        drawButtonText(canvas)
        drawLoadingCircle(canvas, progress)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        addProgressMaxValue(widthSize.toFloat())
        setLoadingCircleDims(widthSize.toFloat(), heightSize.toFloat())
        setMeasuredDimension(w, h)
    }

    private fun drawProgressBar(canvas: Canvas?, progress: Float) {
        //Log.d("drawRectangle", progress.toString())
        paint.color = context.getColor(R.color.colorPrimaryDark)
        canvas?.drawRect(0f, 0f, progress, heightSize.toFloat(), paint)
    }

    private fun drawButtonText(canvas: Canvas?) {
        paint.color = Color.WHITE
        val buttonText = when(buttonState) {
            ButtonState.Completed -> context.getString(R.string.button_name)
            else -> context.getString(R.string.button_loading)
        }
        canvas?.drawText(
                buttonText,
                widthSize.toFloat()/2,
                heightSize.toFloat()/2 - ((paint.descent() + paint.ascent()) / 2) , //((paint.descent() + paint.ascent()) / 2) is the distance from the baseline to the center.: https://stackoverflow.com/questions/11120392/android-center-text-on-canvas
                paint
        )
    }

    private fun drawLoadingCircle(canvas: Canvas?, progress: Float) {
        paint.color = context.getColor(R.color.colorAccent)
        canvas?.drawArc(rectF, 0f, (360f * (progress / 1000f)), true, paint)
    }

    private fun addProgressMaxValue(maxValue: Float){
        valueAnimator.setFloatValues(0f, maxValue)
    }

    private fun setLoadingCircleDims(w: Float, h: Float) {
        val area = w * h / 2
        rectF.bottom = h/2 + (0.0006f * area)/2
        rectF.top = h/2 - (0.0006f * area)/2
        rectF.right = w * 2/3f
        rectF.left = w * 2/3f - (0.0006f * area)
    }
}