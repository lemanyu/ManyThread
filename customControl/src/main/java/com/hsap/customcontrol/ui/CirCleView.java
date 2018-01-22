package com.hsap.customcontrol.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hsap.customcontrol.R;

/**
 * Created by zhao on 2018/1/22.
 */

public class CirCleView extends View {
    private Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mColor=Color.RED;
    public CirCleView(Context context) {
        super(context);
        init();
    }

    public CirCleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirCleView);
        mColor=typedArray.getColor(R.styleable.CirCleView_circle_color,Color.RED);
        typedArray.recycle();
        init();
    }

    public CirCleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirCleView);
        mColor=typedArray.getColor(R.styleable.CirCleView_circle_color,Color.RED);
        typedArray.recycle();
        init();

    }

    private void init() {
        mPaint.setColor(mColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int width = getWidth()-paddingLeft-paddingRight;
        int height = getHeight()-paddingTop-paddingBottom;
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(paddingLeft+width/2,paddingTop+height/2,radius,mPaint);
    }

}
