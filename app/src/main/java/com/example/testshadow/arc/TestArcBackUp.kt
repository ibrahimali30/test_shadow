package com.example.testshadow.arc

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.ViewCompat.setLayerType
import com.google.android.material.button.MaterialButton


class TestArcBackUp(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    MaterialButton(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    fun init(context: Context) {


    }

    val path = Path()
    val radius = 80f
    val viewWidth by lazy { measuredWidth.toFloat() }
    val viewHeight by lazy { measuredHeight.toFloat() }
    val linePaint by lazy {
        val shadow = 70.toFloat()
        val dy = -shadow
        val shadowColor1 = Color.RED
        Paint().also {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.setShadowLayer(shadow, shadow, dy, shadowColor1)
        }
    }

    val paint2 by lazy {
        val shadow = 22.toFloat()
        val dy = -30f
        val dx = 0f
        val shadowColor1 = Color.BLACK
        Paint().also {
            it.style = Paint.Style.FILL
            it.isAntiAlias = true
            it.setShadowLayer(shadow, dx, dy, shadowColor1)
        }
    }




    private fun drawRightLine(canvas: Canvas) {

    }

    private fun drawTopLine(canvas: Canvas) {
        path.apply {
            moveTo(0F, radius)
            arcTo(0F, 0F, 2 * radius, 2 * radius, 180F, 90F, false)
            lineTo(viewWidth - radius, 0F)
            arcTo(viewWidth - 2 * radius, 0F, viewWidth, 2 * radius, -90F, 90F, false)

        }

        canvas.drawPath(path, paint2)
    }



    private fun drawFullSHadow(canvas: Canvas) {
        path.apply {
            moveTo(radius, 0F)
            lineTo(viewWidth - radius, 0F)
            arcTo(viewWidth - 2 * radius, 0F, viewWidth, 2 * radius, -90F, 90F, false)
            lineTo(viewWidth, radius)
            arcTo(viewWidth - 2 * radius, viewHeight - 2 * radius, viewWidth, viewHeight, 0F, 90F, false)
            lineTo(radius, viewHeight)
            arcTo(0F, viewHeight - 2 * radius, 2 * radius, viewHeight, 90F, 90F, false)
            lineTo(0F, radius)
            arcTo(0F, 0F, 2 * radius, 2 * radius, 180F, 90F, false)
        }

        canvas?.drawPath(path, linePaint)
    }

    private fun drawShadow(canvas: Canvas) {

//        this.cornerRadius = 20
//        drawFullSHadow(canvas)
        drawTopLine(canvas)
        drawRightLine(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?: return

//        drawShadow(canvas)
    }

    override fun draw(canvas: Canvas?) {

        canvas?: return

        drawShadow(canvas)
        super.draw(canvas)
    }

    companion object {
        var DENSITY = 1f
        const val SHADOW_DISTANCE = 10f
        const val SHADOW_COLOR = Color.RED
    }

    init {
        init(context)
    }
}