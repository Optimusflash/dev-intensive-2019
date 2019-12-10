package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import ru.skillbranch.devintensive.extensions.pxToDp
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
): ImageView(context, attrs, defStyleAttrs) {

    companion object{
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = context.dpToPx(DEFAULT_BORDER_WIDTH)

    private var bitmapShader: BitmapShader? = null
    private var bitmap: Bitmap? = null
    private var isInitialized: Boolean
    private var borderBounds: RectF
    private var bitmapDrawBounds: RectF
    private var borderPaint: Paint
    private var bitmapPaint: Paint
    private var shaderMatrix: Matrix



    init {
        if (attrs!=null){
            Log.e("M_ImageAvatarView", "init attrs!=null")
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = typedArray.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = typedArray.getDimension(R.styleable.CircleImageView_cv_borderWidth, context.dpToPx(DEFAULT_BORDER_WIDTH))
            typedArray.recycle()
        }

        bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderBounds = RectF()
        bitmapDrawBounds = RectF()
        shaderMatrix = Matrix()

        isInitialized = true

        setup()
        setupBitmap()
    }


    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        Log.e("M_ImageAvatarView", "onDraw")
        //super.onDraw(canvas)
        canvas.drawOval(bitmapDrawBounds,bitmapPaint)
        if (borderPaint.strokeWidth > 0f) {
            canvas.drawOval(borderBounds, borderPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.e("M_ImageAvatarView", "onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        updateBorder()
        updateBitmap()
    }

    private fun updateBorder() {
        Log.e("M_ImageAvatarView", "updateBorder")
        val halfBorderWidth = borderWidth / 2f
        updateCircleDrawBounds(bitmapDrawBounds)
        borderBounds.set(bitmapDrawBounds)
        borderBounds.inset(halfBorderWidth, halfBorderWidth)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        Log.e("M_ImageAvatarView", "updateCircleDrawBounds")

        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setup() {
        Log.e("M_ImageAvatarView", "setup")
        //     if (width == 0 && height == 0) return

        scaleType = SCALE_TYPE

        with(borderPaint){
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }

    }

    private fun setupBitmap(){
        Log.e("M_ImageAvatarView", "setupBitmap")
        super.setScaleType(SCALE_TYPE)

        if (!isInitialized) {
            return
        }
        bitmap = getBitmapFromDrawable(drawable)

        if (bitmap == null){
            Log.e("M_ImageAvatarView", "setupBitmap bitmap == null returned")
            return
        }


        bitmapShader = BitmapShader(bitmap!!,Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapPaint.shader = bitmapShader

        updateBitmap()

    }

    private fun updateBitmap() {
        Log.e("M_ImageAvatarView", "updateBitmap")
        if (bitmap == null){
            Log.e("M_ImageAvatarView", "updateBitmap bitmap == null returned")
            return
        }

        val dx: Float
        val dy: Float
        val scale: Float

        if (bitmap!!.width < bitmap!!.height) {
            scale = bitmapDrawBounds.width() / bitmap!!.width
            dx = bitmapDrawBounds.left
            dy = bitmapDrawBounds.top - bitmap!!.height * scale / 2f + bitmapDrawBounds.width() / 2f
        } else {
            scale = bitmapDrawBounds.height() / bitmap!!.height
            dx = bitmapDrawBounds.left - bitmap!!.width * scale / 2f + bitmapDrawBounds.width() / 2f
            dy = bitmapDrawBounds.top
        }
        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(dx, dy)
        bitmapShader?.setLocalMatrix(shaderMatrix)
        if(isInitialized)invalidate()

    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap?{
        Log.e("M_ImageAvatarView", "getBitmapFromDrawable")
        if (drawable == null) {
            Log.e("M_ImageAvatarView", "getBitmapFromDrawable drawable == null returned")
            return null
        }

        if (drawable is BitmapDrawable){
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight, BITMAP_CONFIG )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0,0,canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    @Dimension
    //fun getBorderWidth(): Int = borderWidth.toDp().toInt()
    fun getBorderWidth(): Int =context.pxToDp(borderWidth).toInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = context.dpToPx(dp)
        updateBorder()
        updateBitmap()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        updateBitmap()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = resources.getColor(colorId, context.theme)
    }

}