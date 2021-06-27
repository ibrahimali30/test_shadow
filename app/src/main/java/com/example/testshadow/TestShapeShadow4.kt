package com.example.testshadow

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton


class TestShapeShadow4 : View {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        // ...
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {}


    var paint: Paint? = null
    var mainColor = 0
    var mShadowColor = 0

    // shadow properties
    var offsetX = -25
    var offsetY = 30
    var blurRadius = 5

    init {
        mainColor = Color.RED
        mShadowColor = Color.BLACK // this color can also have alpha
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?:return

//        val x1 = 200f
//        val y1 = 100f
//        val x2 = 320f
//        val y2 = 0f
//        val shader: Shader = LinearGradient(
//            x1,
//            y1,
//            x2,
//            y2,
//            Color.WHITE,
//            Color.BLACK,
//            TileMode.CLAMP
//        )
        val paint = Paint()
        paint.setAntiAlias(true)

        val endColor = ContextCompat.getColor(context , R.color.end_color)
        val startColor = ContextCompat.getColor(context , R.color.start_color)
        paint.shader = LinearGradient(
            0f,0f, 0f, 50f,
            endColor, startColor, TileMode.CLAMP
        )

        val paint2 = Paint()
        paint2.setColor(Color.GREEN)
//        canvas.drawRect(width.toFloat()+100, -50f, 0f, 50f, paint)
        canvas.drawRect(width.toFloat() + 20, -7f, -0f, 20f, paint)
//        canvas.drawRect(220f, 0f, 400f, 110f, paint)

        super.draw(canvas)
    }

}