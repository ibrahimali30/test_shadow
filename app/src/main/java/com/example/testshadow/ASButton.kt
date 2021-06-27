package com.example.testshadow

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

class ASButton : MaterialButton {

    private val bgPaint: Paint = Paint()

    init {
        bgPaint.color = Color.GREEN
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs, defStyleAttr)
    }

    fun updatePaintShadow(radius: Float, dx: Float, dy: Float, color: Int) {

        update()

        bgPaint.setShadowLayer(value, dx, dy, color)
        this.invalidate()
    }

    var mBackgroundColor = Color.BLACK
    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        bgPaint.color = this.mBackgroundColor
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL

    }



    private val rect = RectF(0f, 0f, 0f, 0f)

    var value = 18.0f
    var shadowMarginLeft = value
    var shadowMarginTop = value
    var shadowMarginRight = value
    var shadowMarginBottom = value

    var cornerRadiusTL = value
    var cornerRadiusTR = value
    var cornerRadiusBR = value
    var cornerRadiusBL = value
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        paint.setShadowLayer(20f, 11f, 11f, Color.BLACK);
// Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        paint.setShadowLayer(20f, -11f, -11f, Color.BLACK);
// Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, paint);


    }


    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        canvas ?: return


    }



    private fun update() {
        value += 1
        shadowMarginLeft = value
        shadowMarginTop = value
        shadowMarginRight = value
        shadowMarginBottom = value

        cornerRadiusTL = value
        cornerRadiusTR = value
        cornerRadiusBR = value
        cornerRadiusBL = value
    }

}




