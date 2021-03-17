import android.graphics.*
import android.graphics.drawable.Drawable
import java.util.*

internal class AvatarPlaceholderDrawable @JvmOverloads constructor(
    private val name: String,
    private var textSizePercentage: Int = DEFAULT_TEXT_SIZE_PERCENTAGE,
    private val defaultString: String = DEFAULT_PLACEHOLDER_STRING
) : Drawable() {

    private val textPaint: Paint
    private val backgroundPaint: Paint
    private var placeholderBounds: RectF? = null
    private val avatarText: String
    private var textStartXPoint = 0f
    private var textStartYPoint = 0f

    init {
        avatarText = convertNameToAvatarText(name)

        textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("white")
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        }
        backgroundPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor(convertStringToColor(name))
        }
    }

    override fun draw(canvas: Canvas) {
        if (placeholderBounds == null) {
            placeholderBounds = RectF(
                0f, 0f, bounds.width().toFloat(),
                bounds.height().toFloat()
            )
            calculateAvatarTextValues()
        }
        canvas.drawRect(placeholderBounds!!, backgroundPaint)
        canvas.drawText(avatarText, textStartXPoint, textStartYPoint, textPaint)
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
        backgroundPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.colorFilter = colorFilter
        backgroundPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private fun calculateAvatarTextValues() {
        textPaint.textSize = calculateTextSize()
        textStartXPoint = calculateTextStartXPoint()
        textStartYPoint = calculateTextStartYPoint()
    }

    private fun calculateTextStartXPoint(): Float {
        val stringWidth = textPaint.measureText(avatarText)
        return bounds.width() / 2f - stringWidth / 2f
    }

    private fun calculateTextStartYPoint(): Float {
        return bounds.height() / 2f - (textPaint.ascent() + textPaint.descent()) / 2f
    }

    private fun convertNameToAvatarText(name: String): String {
        return if (name.isNotEmpty()) name.substring(0, 1)
            .toUpperCase(Locale.getDefault()) else defaultString
    }

    private fun convertStringToColor(text: String): String {
        return if (text.isEmpty()) DEFAULT_PLACEHOLDER_COLOR else String.format(
            COLOR_FORMAT,
            0xFFFFFF and text.hashCode()
        )
    }

    private fun calculateTextSize(): Float {
        if (textSizePercentage < 0 || textSizePercentage > 100) {
            textSizePercentage = DEFAULT_TEXT_SIZE_PERCENTAGE
        }
        return bounds.height() * textSizePercentage.toFloat() / 100
    }

    companion object {
        const val DEFAULT_PLACEHOLDER_STRING = "-"
        private const val DEFAULT_PLACEHOLDER_COLOR = "#3F51B5"
        private const val COLOR_FORMAT = "#FF%06X"
        const val DEFAULT_TEXT_SIZE_PERCENTAGE = 33
    }
}