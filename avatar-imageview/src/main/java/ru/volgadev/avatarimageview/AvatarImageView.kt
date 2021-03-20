package ru.volgadev.avatarimageview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import kotlin.math.min

private const val BORDER_ANIMATION_DURATION_MS = 1300L
private const val BORDER_ANIMATION_AMPLITUDE_PX = 2

class AvatarImageView(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private var borderWidth = 0
    private var textSizePercentage = 33
    private var viewSize = 0
    var circleRadius = 0
    var circleCenterX = 0
    var circleCenterY = 0

    private var name: String = AvatarPlaceholderDrawable.DEFAULT_PLACEHOLDER_STRING
    private val avatarPlaceholderDrawable: AvatarPlaceholderDrawable by lazy {
        AvatarPlaceholderDrawable(name = name, textSizePercentage = textSizePercentage)
    }

    private val borderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    private val mainPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    private var circleRect = Rect(0, 0, 0, 0)

    private var isPlaceholderInFront = true

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AvatarImageView,
            0, 0
        )
        try {
            parseBorderValues(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private fun parseBorderValues(typedArray: TypedArray) {
        val borderColor =
            typedArray.getColor(R.styleable.AvatarImageView_border_color, borderPaint.color)
        borderPaint.color = borderColor
        borderWidth = typedArray.getDimensionPixelSize(
            R.styleable.AvatarImageView_border_width,
            borderWidth
        )
        textSizePercentage = typedArray.getInt(
            R.styleable.AvatarImageView_text_size_percentage, textSizePercentage
        )
    }

    fun setUserName(name: String) {
        this.name = name
    }

    fun setBorderColor(color: Int) {
        borderPaint.color = color
    }

    fun setBorderWidth(widthPx: Int) {
        borderWidth = widthPx
    }

    public override fun onDraw(canvas: Canvas) {
        calculateSizes(canvas)

        var fromPlaceholderToDrawable = false

        val drawable = if (drawable != null) {
            if (isPlaceholderInFront) {
                fromPlaceholderToDrawable = true
                isPlaceholderInFront = false
            }
            drawable
        } else {
            avatarPlaceholderDrawable
        }
        val circleBitmap = cutIntoCircle(drawableToBitmap(drawable))
        canvas.translate(circleCenterX.toFloat(), circleCenterY.toFloat())

        /* draw border */
        canvas.drawCircle(
            (circleRadius + borderWidth).toFloat(),
            (circleRadius + borderWidth).toFloat(),
            (circleRadius + borderWidth).toFloat(),
            borderPaint
        )
        canvas.drawBitmap(circleBitmap, 0f, 0f, null)

        if (fromPlaceholderToDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                animateSettingDrawable()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun animateSettingDrawable() {
        val view = this
        ValueAnimator.ofInt(borderWidth, (borderWidth + BORDER_ANIMATION_AMPLITUDE_PX), borderWidth)
            .apply {
                duration = BORDER_ANIMATION_DURATION_MS
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    view.borderWidth = value
                    view.invalidate()
                }
                start()
            }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(viewSize, viewSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, viewSize, viewSize)
        drawable.draw(canvas)
        return bitmap
    }

    private fun cutIntoCircle(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(viewSize, viewSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(
            (circleRadius + borderWidth).toFloat(),
            (circleRadius + borderWidth).toFloat(),
            circleRadius.toFloat(),
            borderPaint
        )
        canvas.drawBitmap(bitmap, circleRect, circleRect, mainPaint)
        return output
    }

    private fun calculateSizes(canvas: Canvas) {
        val viewHeight = canvas.height
        val viewWidth = canvas.width
        viewSize = min(viewWidth, viewHeight)
        circleCenterX = (viewWidth - viewSize) / 2
        circleCenterY = (viewHeight - viewSize) / 2
        circleRadius = (viewSize - borderWidth * 2) / 2
        circleRect = Rect(0, 0, viewSize, viewSize)

        borderWidth = min(borderWidth, viewSize / 3)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }
}