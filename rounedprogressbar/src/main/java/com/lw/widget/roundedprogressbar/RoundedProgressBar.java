package com.lw.widget.roundedprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by yjwfn on 16-1-8.
 */
public class RoundedProgressBar extends View {

    @ColorInt
    private int     mProgressColor = Color.GREEN;

    @ColorInt
    private int     mBackgroundColor = Color.BLUE;

    private float   mProgressRadius = 10;

    private int     mProgress = 50;

    private int     mMaxProgress = 100;

    private Paint   mPaint;

    private int     mGravity = Gravity.BOTTOM;


    private RectF    mTmpRect;

    private Rect   mProgressBounds;

    private AbsTracker mProgressTracker;

    public RoundedProgressBar(Context context) {
        this(context, null);
    }

    public RoundedProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet){

        TypedArray  typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RoundedProgressBar);
        mBackgroundColor = typedArray.getColor(R.styleable.RoundedProgressBar_colorBackground, Color.BLUE);
        mProgressColor = typedArray.getColor(R.styleable.RoundedProgressBar_colorProgress ,Color.YELLOW);
        //mProgressRadius = typedArray.getDimension(R.styleable.RoundedProgressBar_radius, 10);
        typedArray.recycle();

        int[] indices = new int[]{
                android.R.attr.max,
                android.R.attr.progress};

        typedArray = context.obtainStyledAttributes(attributeSet, indices);
        int maxProgress = typedArray.getInt(typedArray.getIndex(0), 100);
        int progress = typedArray.getInt(typedArray.getIndex(1), mMaxProgress / 2);
        setProgress(progress);
        setMax(maxProgress);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        mProgressBounds = new Rect();
        mTmpRect = new RectF();

       // mProgressTracker = new DrawableWithTextTracker(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawProgress(canvas);
        drawTracker(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode  = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize  = MeasureSpec.getSize(heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumHeight(), widthMeasureSpec);
        int suggestedHeight = getSuggestedMinimumHeight();

        ViewGroup.LayoutParams lp = getLayoutParams();
        if(lp == null){
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int actualHeight = lp.height;
        switch (heightMode){
            case MeasureSpec.AT_MOST:
                if(actualHeight == ViewGroup.LayoutParams.MATCH_PARENT)
                    actualHeight = heightSize;
                else if(actualHeight == ViewGroup.LayoutParams.WRAP_CONTENT)
                    actualHeight = Math.min(suggestedHeight, heightSize);
                break;
            case MeasureSpec.EXACTLY:
                actualHeight = heightSize;
                break;
            default:
                actualHeight = suggestedHeight;
                break;
        }

        actualHeight += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, actualHeight);
     }



    private void drawProgress(Canvas canvas){

        int count = canvas.save();
        //draw bg
        mPaint.setColor(mBackgroundColor);
        mTmpRect.set(mProgressBounds);
        canvas.drawRoundRect(mTmpRect, mProgressRadius, mProgressRadius, mPaint);

        //draw progress
        float scale = (float) mProgress / mMaxProgress;
        mTmpRect.right = mTmpRect.left + mTmpRect.width() * scale;
        mPaint.setColor(mProgressColor);
        canvas.drawRoundRect(mTmpRect, mProgressRadius , mProgressRadius, mPaint);
        canvas.restoreToCount(count);
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(changed){

            int progressHeight = getHeight() - getPaddingBottom() - getPaddingTop();
            if(mProgressBounds == null)
                mProgressBounds = new Rect();

            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth()  - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();

            if(mProgressTracker != null && mProgressTracker.shouldAlign()){
                mProgressTracker.getBounds(mTmpRect);

                left += mTmpRect.width() / 2;
                right -= mTmpRect.width() / 2;
            }


            if((mGravity & Gravity.TOP) == Gravity.TOP){
                mProgressBounds.set(left, top, right, top + progressHeight);
            }else if((mGravity & Gravity.BOTTOM) == Gravity.BOTTOM){
                top = bottom - progressHeight;
                bottom = top + progressHeight;
                mProgressBounds.set(left, top, right, bottom);
            }else {
                top -= (left + bottom - progressHeight) / 2;
                mProgressBounds.set(left, top, right, top + progressHeight);
            }

        }

    }

    private void drawTracker(Canvas canvas){
        if(mProgressTracker != null){
            mProgressTracker.draw(canvas,this);
        }

    }



    public boolean setProgress(int progress){
        progress = constrain(progress);
        if(progress == mProgress)
            return false;

        mProgress = progress;
        refreshProgress(mProgress);

        return true;
    }


    public int getProgress(){
        return mProgress;
    }

    public int getMax(){
        return mMaxProgress;
    }

    public int getGravity(){
        return mGravity;
    }

    public void setMax(int max){

        if(mMaxProgress == max)
            return;


        this.mMaxProgress = max;
        mProgress = constrain(mProgress);
        refreshProgress(mProgress);
    }

    public void  setProgressBounds(Rect outBounds){
        if(mProgressBounds != null && outBounds != null)
            outBounds.set(mProgressBounds);
    }



    private int constrain(int progress){
        if(progress >= 0 && progress <= mMaxProgress){
            return progress;
        }

        return Math.max(0, Math.min(progress, mMaxProgress));
    }



    private void refreshProgress(int progress){
        postInvalidate();
    }


    public static abstract class AbsTracker {

        public abstract void    draw(Canvas canvas, RoundedProgressBar progressBar);

        public  boolean shouldAlign(){return true;}

        public abstract  void    getBounds(RectF outRect);

    }
}
/*


class DrawableWithTextTracker extends TextProgressBar.AbsTracker {

    Paint mPaint;

    Drawable mDrawable;

    public DrawableWithTextTracker(Context context) {

        mPaint = new Paint();
        mPaint.setTextSize(30);
        mPaint.setColor(Color.WHITE);

        mDrawable  = ResourcesCompat.getDrawable(context.getResources(), R.mipmap.home_flag, null);
    }

    @Override
    public void draw(Canvas canvas, TextProgressBar progressBar) {

        Rect progressLoc = new Rect();
        progressBar.setProgressBounds(progressLoc);
        float scale  = (float) progressBar.getProgress() / progressBar.getMax();

        int left = (int) (progressLoc.left + progressLoc.width() * scale);
        left -= mDrawable.getMinimumWidth() / 2;
        int right = left + mDrawable.getMinimumWidth();
        int top = progressLoc.top - mDrawable.getIntrinsicHeight();
        int bottom = top + mDrawable.getIntrinsicHeight();

        mDrawable.setBounds(left, top, right, bottom);
        mDrawable.draw(canvas);
        progressLoc.set(left, top, right, bottom);
        drawText(canvas, progressLoc, progressBar.getProgress());
    }


    private void drawText(Canvas canvas, Rect bounds, int progress){
        String text = progress + "";
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int textHeight = fontMetricsInt.descent - fontMetricsInt.ascent;
        mPaint.setTextAlign(Paint.Align.CENTER);
        int x = (bounds.left + bounds.right) / 2;
        int y = (bounds.top + bounds.bottom ) / 2 ;
        canvas.drawText(text, x, y, mPaint);
    }

    @Override
    public boolean shouldAlign() {
        return true;
    }

    @Override
    public void getBounds(RectF outRect) {
        outRect.set(0, 0 , mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
    }


}
*/
