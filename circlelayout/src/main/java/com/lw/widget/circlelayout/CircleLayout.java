package com.lw.widget.circlelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by yjwfn on 15-12-18.
 */
public class CircleLayout extends FrameLayout{


    private Paint   mPaint;

    private float   mProgress;

    private int     mStrokeWidth;

    private int     mStartAngle;

    private int     mEndAngle;

    private int     mStartColor;
    private int     mEndColor;
    private int     mPointColor;

    private int     mCircleBackground;





    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs, defStyleAttr);
    }

    private void    initView(Context context, AttributeSet attrs, int defStyleAttr){

        TypedArray  typedArray  = context.obtainStyledAttributes(attrs, R.styleable.CircleLayout, defStyleAttr,0);
        mProgress = typedArray.getFloat(R.styleable.CircleLayout_progress, 0f);
        mStrokeWidth = (int) typedArray.getDimension(R.styleable.CircleLayout_strokeWidth, 20);
        mStartAngle = typedArray.getInteger(R.styleable.CircleLayout_startSweepAngle, 45);
        mEndAngle = typedArray.getInteger(R.styleable.CircleLayout_endSweepAngle, 315);
        mStartColor = typedArray.getColor(R.styleable.CircleLayout_startColor, Color.TRANSPARENT);
        mEndColor = typedArray.getColor(R.styleable.CircleLayout_endColor, Color.TRANSPARENT);
        mPointColor = typedArray.getColor(R.styleable.CircleLayout_pointColor, Color.TRANSPARENT);
        mCircleBackground = typedArray.getColor(R.styleable.CircleLayout_circleBackground, Color.BLACK);
        typedArray.recycle();



        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
    }



     protected void drawCircle(Canvas canvas) {

         int offset = mStrokeWidth / 2;
         int size = getHeight();
         RectF drawArea = new RectF(offset, offset, size - offset, size  - offset);

        int    centerX = size / 2;
        int    centerY = size  / 2;

        int savePoint = canvas.save();
        canvas.rotate(-90, centerX, centerY);
        mPaint.setStyle(Paint.Style.STROKE);
         if(mEndAngle - mStartAngle >= 360)
            mPaint.setStrokeCap(Paint.Cap.BUTT);
         else
            mPaint.setStrokeCap(Paint.Cap.ROUND);

        if(mPaint.getShader() != null)
            mPaint.setShader(null);

        //draw background
        int sweepDegree = mEndAngle - mStartAngle;
        mPaint.setColor(mCircleBackground);
        canvas.drawArc(drawArea, mStartAngle, sweepDegree, false, mPaint);

         //draw progress
        Shader progressShader = new SweepGradient(centerX, centerY, new int[]{mStartColor, mEndColor}, new float[]{0, mProgress});
        sweepDegree *= mProgress;
        mPaint.setShader(progressShader);
        canvas.drawArc(drawArea, mStartAngle, sweepDegree, false, mPaint);


         //draw point
        mPaint.setColor(mPointColor);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.FILL);
        //不需要旋转,因为它们都是从左上角开始的
        canvas.rotate(0);
        float x = (float) Math.cos(Math.toRadians(mStartAngle + sweepDegree ) );
        float y = (float) Math.sin(Math.toRadians(mStartAngle + sweepDegree  )  );

        int pointSize = (int) (mStrokeWidth * 0.6);
        int tmpRadius = (int) (drawArea.height() / 2);
        RectF   pointRect = new RectF();
        pointRect.left = centerX + x * tmpRadius - pointSize / 2 ;
        pointRect.right =  (pointRect.left + pointSize);
        pointRect.top = centerY + y *  tmpRadius - pointSize / 2;
        pointRect.bottom = (pointRect.top + pointSize);
        canvas.drawOval(pointRect, mPaint);
        canvas.restoreToCount(savePoint);
    }



    public void setProgress(float progress){


    }


    public void setStartColor(@ColorInt int color){
        mStartColor = color;
    }

    public void setEndColor(@ColorInt int color){
        mEndAngle = color;
    }

    public void setDotColor(@ColorInt int color){
        mPointColor = color;
    }

}
