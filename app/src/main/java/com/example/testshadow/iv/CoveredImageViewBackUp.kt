package com.example.testshadow.iv

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat.setLayerType


class CoveredImageViewBackUp(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    lateinit var path: Path
    lateinit var paint: Paint
    lateinit var p1: Point
    lateinit var p2: Point
    lateinit var p3: Point

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    fun init(context: Context) {
        DENSITY = context.getResources().getDisplayMetrics().density
        path = Path()
        paint = Paint()
        p1 = Point()
        p2 = Point()
        p3 = Point()

        // Required to make the ShadowLayer work properly
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun updateDrawVariables() {
        val shadowSize = (SHADOW_DISTANCE * DENSITY).toInt()
        paint.setColor(Color.WHITE)
        paint.setStyle(Paint.Style.FILL)
        paint.setAntiAlias(true)
        paint.setShadowLayer(shadowSize.toFloat(), 0.toFloat(), -1.toFloat(), SHADOW_COLOR)

        // Offset the actual position by the shadow size so
        val left = 0 - shadowSize
        val right: Int = getMeasuredWidth() + shadowSize
        val bottom: Int = getMeasuredHeight()
        p1.set(left, bottom)
        p2.set(right, bottom - (52 * DENSITY).toInt())
        p3.set(right, bottom)
        path.setFillType(Path.FillType.EVEN_ODD)
        path.moveTo(p1.x.toFloat(), p1.y.toFloat())
        path.lineTo(p2.x.toFloat(), p2.y.toFloat())
        path.lineTo(p3.x.toFloat(), p3.y.toFloat())
        // Move the path shape down so that the shadow doesn't "fade" at the left and right edges
        path.lineTo(p3.x.toFloat(), p3.y.toFloat() + shadowSize)
        path.lineTo(p1.x.toFloat(), p1.y + shadowSize.toFloat())
        path.close()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Update all the drawing variables if the layout values have changed
        if (changed) {
            updateDrawVariables()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Paint the current path values that were set after onLayout()
        canvas.drawPath(path, paint)
    }

    companion object {
        var DENSITY = 1f
        const val SHADOW_DISTANCE = 100f
        const val SHADOW_COLOR = Color.RED
    }

    init {
        init(context)
    }
}