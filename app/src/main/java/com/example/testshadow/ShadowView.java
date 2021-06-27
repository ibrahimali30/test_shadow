package com.example.testshadow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

public class ShadowView extends MaterialButton {


    public ShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.GREEN);
        setCornerRadius(10);
    }

    public ShadowView(Context context) {
        super(context);
        initView();
    }



    private Paint createShadow() {

        initView();

        Paint mShadow = new Paint();

        float radius = 10.0f;
        float xOffset = 5.0f;
        float yOffset = 5.0f;
        mShadow.setColor(Color.RED);
        // color=black
        int color = 0xFF000000;
        int redColor = Color.RED;
        mShadow.setShadowLayer(radius, xOffset, yOffset, redColor);


        return mShadow;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mShadow = createShadow();
        setLayerType(LAYER_TYPE_SOFTWARE, mShadow);
//        canvas.drawPaint(mShadow);


//
//        initView();
//
        super.onDraw(canvas);
//        initView();


    }

}