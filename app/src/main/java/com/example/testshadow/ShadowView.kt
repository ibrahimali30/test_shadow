//package com.example.testshadow
//
//import android.R.attr.bitmap
//import android.content.Context
//import android.graphics.*
//import android.util.AttributeSet
//import com.google.android.material.button.MaterialButton
//
//
//class ShadowView : MaterialButton {
//
//    private val bgPaint: Paint = Paint()
//
//    init {
//        bgPaint.color = Color.GREEN
//    }
//
//    constructor(context: Context) : super(context) {
//        initView(context)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        initView(context, attrs)
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context!!,
//        attrs,
//        defStyleAttr
//    ) {
//        initView(context, attrs, defStyleAttr)
//    }
//
//
//    var mBackgroundColor = Color.RED
//    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
//        bgPaint.color = this.mBackgroundColor
//        bgPaint.isAntiAlias = true
//        bgPaint.style = Paint.Style.FILL
//
//    }
//
//    val mShadow = Paint()
//
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//
//
//        canvas ?: return
//
//        mShadow.setShadowLayer(20.0f, 5.0f, 5.0f, -0x1000000)
//
//        canvas.drawPaint(mShadow)
//
//
//    }
//
//
//}
//
//
//
//
